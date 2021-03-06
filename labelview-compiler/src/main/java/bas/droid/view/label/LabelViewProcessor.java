package bas.droid.view.label;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

import bas.droid.view.label.annotation.LabelView;
import bas.lib.compiler.AnnotationUtils;
import bas.lib.compiler.BaseProcessor;
import bas.lib.compiler.DroidMethodUtils;
import bas.lib.compiler.ParamsUtils;

/**
 * LabelView系列生成处理器：文件名规则-在原有类型的末尾增加LabelView或者将View替换成LabelView
 */
@AutoService(Processor.class)
public class LabelViewProcessor extends BaseProcessor {

    private static final String TAG = "LabelViewProcessor";

    private static final String CLASS_JAVA_DOC = "Generated by bas-leanback-layout-compiler. Do not edit it!\n";
    private static final String PACKAGE_NAME = "bas.droid.view.label";
    private static final String CLASS_SUFFIX = "LabelView";

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> annotationTypes = new LinkedHashSet<>();
        annotationTypes.add(LabelView.class.getCanonicalName());
        return annotationTypes;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(LabelView.class);
        if (elements == null || elements.isEmpty()) {
            printMessage("process interrupt:elements is empty.");
            return true;
        }
        printDividerMessage(TAG + " process START");
        Set<String> viewClassSet = new HashSet<>();
        parseParams(elements, viewClassSet);
        try {
            generateClasses(viewClassSet);
        } catch (IllegalAccessException e) {
            printErrorMessage("IllegalAccessException occurred when generating class file.");
            e.printStackTrace();
        } catch (IOException e) {
            printErrorMessage("IOException occurred when generating class file.");
            e.printStackTrace();
        }
        printDividerMessage(TAG + " process END");
        return true;
    }

    private void generateClasses(Set<String> viewClassSet) throws IllegalAccessException, IOException {
        printMessage(String.format("准备生成%d个文件", viewClassSet.size()));
        for (String clazz : viewClassSet) {
            int lastDotIndex = clazz.lastIndexOf(".");
            String superPackageName = clazz.substring(0, lastDotIndex);
            String superClassName = clazz.substring(lastDotIndex + 1);
            String className = generateClassName(superClassName);

            printMessage(String.format("正在生成 %s ====>  %s", clazz, className));

            TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(className)
                    .addJavadoc(CLASS_JAVA_DOC)
                    .addModifiers(Modifier.PUBLIC)
                    .superclass(ClassName.get(superPackageName, superClassName))
//                    .addSuperinterface(ClassName.get(PACKAGE_NAME, "LeanbackLayoutHelper.Callback"))
                    .addField(ClassName.get(PACKAGE_NAME, "LabelViewHelper"), "labelHelper", Modifier.PRIVATE, Modifier.FINAL);

            generateMethods(typeBuilder, clazz);

            JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, typeBuilder.build()).build();
            javaFile.writeTo(filer);
        }
    }

    private String generateClassName(String superClassName) {
        if (superClassName.endsWith("View")) {
            return superClassName.replace("View", CLASS_SUFFIX);
        } else {
            return superClassName + CLASS_SUFFIX;
        }
    }

    private void generateMethods(TypeSpec.Builder typeBuilder, String clazz) {
        DroidMethodUtils.viewConstructorOverloads(typeBuilder, clazz, constructorCode());
        if ("android.view.View".equalsIgnoreCase(clazz)) {
            onMeasure(typeBuilder, clazz);
        }
        onSizeChanged(typeBuilder, clazz);
        onDraw(typeBuilder, clazz);
        labelHelperApi(typeBuilder, clazz);
    }

    private void onMeasure(TypeSpec.Builder typeBuilder, String clazz) {
        MethodSpec onMeasure = MethodSpec.methodBuilder("onMeasure")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(int.class, "widthMeasureSpec")
                .addParameter(int.class, "heightMeasureSpec")
                .addStatement("int width = labelHelper.measureWidth(widthMeasureSpec)")
                .addStatement("int height = labelHelper.measureHeight(heightMeasureSpec)")
                .addStatement("setMeasuredDimension(width,height)")
                .build();
        typeBuilder.addMethod(onMeasure);
    }

    private CodeBlock constructorCode() {
        return CodeBlock.builder()
                .addStatement("super(context, attrs, defStyleAttr)")
                .addStatement("labelHelper = new LabelViewHelper(this, attrs, defStyleAttr);")
                .build();
    }

    private void onSizeChanged(TypeSpec.Builder typeBuilder, String clazz) {
        MethodSpec onSizeChanged = DroidMethodUtils.onSizeChangedSignature()
                .addStatement("super.onSizeChanged(w, h, oldw, oldh)")
                .addStatement("labelHelper.onSizeChanged(w, h, oldw, oldh)")
                .build();
        typeBuilder.addMethod(onSizeChanged);
    }

    private void onDraw(TypeSpec.Builder typeBuilder, String clazz) {
        MethodSpec onDraw = DroidMethodUtils.onDrawSignature()
                .addStatement("super.onDraw(canvas)")
                .addStatement("labelHelper.onDraw(canvas)")
                .build();
        typeBuilder.addMethod(onDraw);
    }

    private void labelHelperApi(TypeSpec.Builder typeBuilder, String clazz) {
        MethodSpec setLabelText = MethodSpec.methodBuilder("setLabelText")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParamsUtils.stringTypeName(true), "text")
                .addStatement("labelHelper.setText(text)")
                .build();
        typeBuilder.addMethod(setLabelText);

        MethodSpec setLabelTextSize = MethodSpec.methodBuilder("setLabelTextSize")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(int.class).annotated(AnnotationUtils.pxAnnotation), "textSizePx")
                .addStatement("labelHelper.setTextSize(textSizePx)")
                .build();
        typeBuilder.addMethod(setLabelTextSize);

        MethodSpec setLabelTextColor = MethodSpec.methodBuilder("setLabelTextColor")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(int.class).annotated(AnnotationUtils.colorIntAnnotation), "textColor")
                .addStatement("labelHelper.setTextColor(textColor)")
                .build();
        typeBuilder.addMethod(setLabelTextColor);


        AnnotationSpec textStyleAnnotation = AnnotationSpec.builder(
                ClassName.get(PACKAGE_NAME, "LabelViewHelper.TextStyle")
        ).build();

        MethodSpec setLabelTextStyle = MethodSpec.methodBuilder("setLabelTextStyle")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(int.class).annotated(textStyleAnnotation), "textStyle")
                .addStatement("labelHelper.setTextStyle(textStyle)")
                .build();
        typeBuilder.addMethod(setLabelTextStyle);


        AnnotationSpec locationAnnotation = AnnotationSpec.builder(
                ClassName.get(PACKAGE_NAME, "LabelViewHelper.Location")
        ).build();

        MethodSpec setLabelLocation = MethodSpec.methodBuilder("setLabelLocation")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(int.class).annotated(locationAnnotation), "location")
                .addStatement("labelHelper.setLocation(location)")
                .build();
        typeBuilder.addMethod(setLabelLocation);

        MethodSpec setLabelDistance = MethodSpec.methodBuilder("setLabelDistance")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(int.class).annotated(AnnotationUtils.pxAnnotation), "distancePx")
                .addStatement("labelHelper.setDistance(distancePx)")
                .build();
        typeBuilder.addMethod(setLabelDistance);

        MethodSpec setLabelBackgroundColor = MethodSpec.methodBuilder("setLabelBackgroundColor")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(int.class).annotated(AnnotationUtils.colorIntAnnotation), "backgroundColor")
                .addStatement("labelHelper.setBackgroundColor(backgroundColor)")
                .build();
        typeBuilder.addMethod(setLabelBackgroundColor);

        MethodSpec setLabelPadding = MethodSpec.methodBuilder("setLabelPadding")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(int.class).annotated(AnnotationUtils.pxAnnotation), "paddingPx")
                .addStatement("labelHelper.setPadding(paddingPx)")
                .build();
        typeBuilder.addMethod(setLabelPadding);

        MethodSpec setLabelStrokeWidth = MethodSpec.methodBuilder("setLabelStrokeWidth")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(int.class).annotated(AnnotationUtils.pxAnnotation), "strokeWidthPx")
                .addStatement("labelHelper.setStrokeWidth(strokeWidthPx)")
                .build();
        typeBuilder.addMethod(setLabelStrokeWidth);

        MethodSpec setLabelStrokeColor = MethodSpec.methodBuilder("setLabelStrokeColor")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(int.class).annotated(AnnotationUtils.colorIntAnnotation), "strokeColor")
                .addStatement("labelHelper.setStrokeColor(strokeColor)")
                .build();
        typeBuilder.addMethod(setLabelStrokeColor);

        MethodSpec setLabelYAxisDegree = MethodSpec.methodBuilder("setLabelYAxisDegree")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(int.class).annotated(AnnotationUtils.createIntRangeAnnotation(0, 90)), "degree")
                .addStatement("labelHelper.setYAxisDegree(degree)")
                .build();
        typeBuilder.addMethod(setLabelYAxisDegree);

        MethodSpec setLabelVisible = MethodSpec.methodBuilder("setLabelVisible")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(boolean.class, "isVisible")
                .addStatement("labelHelper.setVisible(isVisible)")
                .build();
        typeBuilder.addMethod(setLabelVisible);
    }

    private void parseParams(Set<? extends Element> elements, Set<String> viewClassSet) {
        for (Element element : elements) {
            checkAnnotationValid(element, LabelView.class);
            TypeElement classElement = (TypeElement) element;
            // 获取该注解的值
            LabelView badgeAnnotation = classElement.getAnnotation(LabelView.class);
            try {
                badgeAnnotation.value();
            } catch (MirroredTypesException e) {
                List<? extends TypeMirror> typeMirrors = e.getTypeMirrors();
                for (TypeMirror typeMirror : typeMirrors) {
                    DeclaredType classTypeMirror = (DeclaredType) typeMirror;
                    TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
                    String qualifiedName = classTypeElement.getQualifiedName().toString();
                    viewClassSet.add(qualifiedName);
                }
            }
        }
    }


}