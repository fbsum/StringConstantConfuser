package com.fbsum.plugin.stringconstantconfuser

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.*

object Utils {

    private val log = Logger.getInstance(Utils::class.java)

    private val STRING_TYPE_NAME: String = "java.lang.String"

    private lateinit var factory: PsiElementFactory
    private var index: Int = 1

    fun confuse(project: Project, psiFile: PsiJavaFile) {
        factory = JavaPsiFacade.getElementFactory(project)
        psiFile.classes.iterator().forEach {
            confuseClass(it)
        }
    }

    private fun confuseClass(psiClass: PsiClass) {
        psiClass.fields
                .filter {
                    it.hasModifierProperty(PsiModifier.FINAL)
                            && it.hasModifierProperty(PsiModifier.STATIC)
                            && StringUtil.equals(it.type.canonicalText, STRING_TYPE_NAME)
                }
                .iterator()
                .forEach {
                    val literalExpression = it.children.find { it is PsiLiteralExpression } as PsiLiteralExpression
                    val newFieldText = it.text.replace(literalExpression.text, "\"${toNumberSystem26(index)}\"")
                    val newField = factory.createFieldFromText(newFieldText, psiClass)
                    it.replace(newField)
                    index++
                }
    }

    private fun toNumberSystem26(num: Int): String {
        var n = num
        var s = ""
        while (n > 0) {
            var m = n % 26
            if (m == 0) m = 26
            s = (m + 96).toChar() + s
            n = (n - m) / 26
        }
        return s
    }
}