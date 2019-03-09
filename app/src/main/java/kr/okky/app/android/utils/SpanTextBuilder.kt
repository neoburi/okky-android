package kr.okky.app.android.utils

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.CharacterStyle
import java.util.*

class SpanTextBuilder {

    private val spanTextSection:ArrayList<SpanTextSection> = ArrayList()
    private val stringBuilder:StringBuilder = java.lang.StringBuilder()

    fun append(text:String, vararg styles:CharacterStyle):SpanTextBuilder{
        styles.let {
            spanTextSection.add(SpanTextSection(text, stringBuilder.length, *it))
        }
        stringBuilder.append(text)
        return this
    }

    fun appendWithSpace(text: String, vararg styles: CharacterStyle): SpanTextBuilder {
        return append("$text ", *styles)
    }

    fun build(): SpannableStringBuilder {
        val builder = SpannableStringBuilder(stringBuilder.toString())
        for (textSection in spanTextSection) {
            textSection.apply(builder)
        }
        return builder
    }

    override fun toString(): String {
        return stringBuilder.toString()
    }

    internal inner class SpanTextSection(private var text: String, private var index: Int, vararg styles: CharacterStyle) {
        private var styles:Array<CharacterStyle>
        init {
            this.styles = arrayOf(*styles)
        }
        fun apply(spanStringBuilder:SpannableStringBuilder? ){
            spanStringBuilder?.let { span ->
                styles.iterator().forEach { value->
                    span.setSpan(value, index, index + text.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                }
            }
        }
    }
}