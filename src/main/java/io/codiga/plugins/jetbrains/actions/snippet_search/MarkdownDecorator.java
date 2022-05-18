package io.codiga.plugins.jetbrains.actions.snippet_search;

import com.github.rjeschke.txtmark.DefaultDecorator;

public class MarkdownDecorator extends DefaultDecorator {

    private static final String style = " style=\"font-family: Arial;\" ";

    @Override
    public void openHeadline(final StringBuilder out, final int level)
    {
        out.append("<h");
        out.append(level + 2);
        out.append(style);
    }

    @Override
    public void openParagraph(final StringBuilder out)
    {

        out.append("<p");
        out.append(style);
        out.append(">");
    }

    @Override
    public void openListItem(final StringBuilder out)
    {

        out.append("<li");
        out.append(style);
        out.append(">");
    }
}
