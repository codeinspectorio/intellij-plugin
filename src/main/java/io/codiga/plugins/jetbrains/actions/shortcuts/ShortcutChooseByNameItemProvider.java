package io.codiga.plugins.jetbrains.actions.shortcuts;

import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.ChooseByNameViewModel;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.util.Processor;
import io.codiga.api.GetRecipesForClientByShortcutQuery;
import io.codiga.api.type.LanguageEnumeration;
import io.codiga.plugins.jetbrains.actions.CodeInsertionContext;
import io.codiga.plugins.jetbrains.graphql.CodigaApi;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static io.codiga.plugins.jetbrains.actions.ActionUtils.*;

public class ShortcutChooseByNameItemProvider implements ChooseByNameItemProvider {

    private final AnActionEvent anActionEvent;
    private final CodeInsertionContext codeInsertionContext;
    private final CodigaApi codigaApi = ApplicationManager.getApplication().getService(CodigaApi.class);

    public ShortcutChooseByNameItemProvider(AnActionEvent anActionEvent, CodeInsertionContext codeInsertionContext) {
        this.anActionEvent = anActionEvent;
        this.codeInsertionContext = codeInsertionContext;
    }

    @Override
    public @NotNull List<String> filterNames(@NotNull ChooseByNameViewModel chooseByNameViewModel, String @NotNull [] strings, @NotNull String s) {
        return null;
    }

    @Override
    public boolean filterElements(@NotNull ChooseByNameViewModel chooseByNameViewModel, @NotNull String s, boolean b, @NotNull ProgressIndicator progressIndicator, @NotNull Processor<Object> processor) {

        String toSearch = s.isEmpty() ? null : s;

        if(!progressIndicator.isRunning()) {
            progressIndicator.start();

        }

        String filename = getRelativeFilenamePathFromEditorForEvent(anActionEvent);
        List<String> dependenciesName = getDependenciesFromEditorForEvent(anActionEvent);
        LanguageEnumeration language = getLanguageFromEditorForEvent(anActionEvent);

        if (language == LanguageEnumeration.UNKNOWN) {
            return false;
        }

        List<GetRecipesForClientByShortcutQuery.GetRecipesForClientByShortcut> newRecipes = codigaApi.getRecipesForClientByShotcurt(
            Optional.ofNullable(toSearch),
            dependenciesName,
            Optional.empty(),
            language,
            filename);


        newRecipes.forEach(processor::process);

        if(progressIndicator.isRunning() && !progressIndicator.isCanceled()) {
            progressIndicator.stop();
        }

        return false;
    }
}
