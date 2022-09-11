package io.codiga.plugins.jetbrains.model.rosie;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

import static io.codiga.plugins.jetbrains.Constants.LOGGER_NAME;

public class RosiePosition {
    public static final Logger LOGGER = Logger.getInstance(LOGGER_NAME);
    public int line;
    public int col;

    public RosiePosition(int line, int col) {
        this.line = line;
        this.col = col;
    }

    public int getOffset(@NotNull Editor editor) {
        Document document = editor.getDocument();
        int offset = document.getLineStartOffset(this.line - 1) + this.col;
        LOGGER.info(String.format("Converting line %s, col %s to offset: %s", this.line, this.col, offset));
        return offset;
    }

}