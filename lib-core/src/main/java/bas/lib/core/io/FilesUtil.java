package bas.lib.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class FilesUtil {

    public static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

    public static long copy(InputStream input, OutputStream out) throws IOException {
        return copy(input, out, DEFAULT_BUFFER_SIZE);
    }

    public static long copy(InputStream input, OutputStream out, int bufferSize) throws IOException {
        long bytesCopied = 0;
        byte[] buffer = new byte[bufferSize];
        int bytes = 0;
        while ((bytes = input.read(buffer)) >= 0) {
            out.write(buffer, 0, bytes);
            bytesCopied += bytes;
        }
        return bytesCopied;
    }

    public static File copy(File input, File target) throws IOException {
        return copy(input, target, false, DEFAULT_BUFFER_SIZE);
    }

    public static File copy(File input, File target, boolean overwrite) throws IOException {
        return copy(input, target, overwrite, DEFAULT_BUFFER_SIZE);
    }

    public static File copy(File input, File target, boolean overwrite, int bufferSize) throws IOException {
        if (!input.exists()) {
            throw new FileNotFoundException("The source file [" + input.getAbsolutePath() + "] doesn't exist.");
        }

        if (target.exists()) {
            if (!overwrite)
                throw new FileAlreadyExistsException(input.getAbsolutePath(), target.getAbsolutePath(), "The destination file already exists.");
            else if (!target.delete())
                throw new FileAlreadyExistsException(input.getAbsolutePath(), target.getAbsolutePath(), "Tried to overwrite the destination, but failed to delete it.");
        }

        if (input.isDirectory()) {
            if (!target.mkdirs())
                throw new FileSystemException(input.getAbsolutePath(), target.getAbsolutePath(), "Failed to create target directory.");
        } else {
            File parent = target.getParentFile();
            if (parent != null)
                parent.mkdirs();
            try (FileInputStream fis = new FileInputStream(input);
                 FileOutputStream fos = new FileOutputStream(target)) {
                copy(fis, fos, bufferSize);
            }
        }
        return target;
    }
}

