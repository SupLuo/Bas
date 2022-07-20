//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package bas.lib.core.io;

import java.io.IOException;

public class FileSystemException extends IOException {
    static final long serialVersionUID = -3055425747967319812L;
    private final String file;
    private final String other;

    public FileSystemException(String file) {
        super((String)null);
        this.file = file;
        this.other = null;
    }

    public FileSystemException(String file, String other, String reason) {
        super(reason);
        this.file = file;
        this.other = other;
    }

    public String getFile() {
        return this.file;
    }

    public String getOtherFile() {
        return this.other;
    }

    public String getReason() {
        return super.getMessage();
    }

    public String getMessage() {
        if (this.file == null && this.other == null) {
            return this.getReason();
        } else {
            StringBuilder sb = new StringBuilder();
            if (this.file != null) {
                sb.append(this.file);
            }

            if (this.other != null) {
                sb.append(" -> ");
                sb.append(this.other);
            }

            if (this.getReason() != null) {
                sb.append(": ");
                sb.append(this.getReason());
            }

            return sb.toString();
        }
    }
}
