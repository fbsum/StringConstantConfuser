package com.fbsum.plugin.stringconstantconfuser

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager


class ConfuseStringConstantAction : AnAction() {

    private val log = Logger.getInstance(ConfuseStringConstantAction::class.java)
    private val VALID_FILE_EXTENSION = "java"

    override fun update(event: AnActionEvent) {
        super.update(event)
        val virtualFile = event.getData(LangDataKeys.VIRTUAL_FILE) ?: return
        event.presentation.isVisible = isValidFile(virtualFile)
    }

    /**
     * 判断当前显示的文件是否为 java 类文件
     */
    private fun isValidFile(file: VirtualFile): Boolean {
        return StringUtil.equals(file.extension, VALID_FILE_EXTENSION)
    }

    override fun actionPerformed(event: AnActionEvent) {
        log.info("begin")
        val virtualFile = event.getData(LangDataKeys.VIRTUAL_FILE) ?: return
        val project = event.getData(PlatformDataKeys.PROJECT) ?: return
        val psiFile: PsiJavaFile = PsiManager.getInstance(project).findFile(virtualFile) as PsiJavaFile
        object : WriteCommandAction.Simple<Any>(project) {
            @Throws(Throwable::class)
            override fun run() {
                Utils.confuse(project, psiFile)
            }
        }.execute()
    }
}
