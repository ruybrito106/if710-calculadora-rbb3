package br.ufpe.cin.if710.calculadora

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setListeners()
    }

    fun setListeners() {

        var text = findViewById(R.id.text_calc) as EditText

        var btn_0 = findViewById(R.id.btn_0) as Button
        var btn_1 = findViewById(R.id.btn_1) as Button
        var btn_2 = findViewById(R.id.btn_2) as Button
        var btn_3 = findViewById(R.id.btn_3) as Button
        var btn_4 = findViewById(R.id.btn_4) as Button
        var btn_5 = findViewById(R.id.btn_5) as Button
        var btn_6 = findViewById(R.id.btn_6) as Button
        var btn_7 = findViewById(R.id.btn_7) as Button
        var btn_8 = findViewById(R.id.btn_8) as Button
        var btn_9 = findViewById(R.id.btn_9) as Button

        var btn_add = findViewById(R.id.btn_Add) as Button
        var btn_sub = findViewById(R.id.btn_Subtract) as Button
        var btn_mul = findViewById(R.id.btn_Multiply) as Button
        var btn_div = findViewById(R.id.btn_Divide) as Button

        var btn_power = findViewById(R.id.btn_Power) as Button
        var btn_lparen = findViewById(R.id.btn_LParen) as Button
        var btn_rparen = findViewById(R.id.btn_RParen) as Button
        var btn_dot = findViewById(R.id.btn_Dot) as Button

        btn_0.setOnClickListener { text.append(btn_0.text) }
        btn_1.setOnClickListener { text.append(btn_1.text) }
        btn_2.setOnClickListener { text.append(btn_2.text) }
        btn_3.setOnClickListener { text.append(btn_3.text) }
        btn_4.setOnClickListener { text.append(btn_4.text) }
        btn_5.setOnClickListener { text.append(btn_5.text) }
        btn_6.setOnClickListener { text.append(btn_6.text) }
        btn_7.setOnClickListener { text.append(btn_7.text) }
        btn_8.setOnClickListener { text.append(btn_8.text) }
        btn_9.setOnClickListener { text.append(btn_9.text) }

        btn_add.setOnClickListener { text.setText(text.text.toString() + "+") }
        btn_sub.setOnClickListener { text.setText(text.text.toString() + "-") }
        btn_mul.setOnClickListener { text.setText(text.text.toString() + "*") }
        btn_div.setOnClickListener { text.setText(text.text.toString() + "/") }

        btn_power.setOnClickListener { text.setText(text.text.toString() + "^") }
        btn_lparen.setOnClickListener { text.setText(text.text.toString() + "(") }
        btn_rparen.setOnClickListener { text.setText(text.text.toString() + ")") }
        btn_dot.setOnClickListener { text.setText(text.text.toString() + ".") }

        var btn_eval = findViewById(R.id.btn_Equal) as Button
        btn_eval.setOnClickListener {
            try {
                var value = eval(text.text.toString())
                var info = findViewById(R.id.text_info) as TextView
                info.text = value.toString()
            } catch (e : RuntimeException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }

        var btn_clear = findViewById(R.id.btn_Clear) as Button
        btn_clear.setOnClickListener {
            text.setText("")
        }

    }

    //Como usar a função:
    // eval("2+2") == 4.0
    // eval("2+3*4") = 14.0
    // eval("(2+3)*4") = 20.0
    //Fonte: https://stackoverflow.com/a/26227947
    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = ' '
            fun nextChar() {
                val size = str.length
                ch = if ((++pos < size)) str.get(pos) else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Caractere inesperado: " + ch)
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            // | number | functionName factor | factor `^` factor
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'))
                        x += parseTerm() // adição
                    else if (eat('-'))
                        x -= parseTerm() // subtração
                    else
                        return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'))
                        x *= parseFactor() // multiplicação
                    else if (eat('/'))
                        x /= parseFactor() // divisão
                    else
                        return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor() // + unário
                if (eat('-')) return -parseFactor() // - unário
                var x: Double
                val startPos = this.pos
                if (eat('(')) { // parênteses
                    x = parseExpression()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') { // números
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = java.lang.Double.parseDouble(str.substring(startPos, this.pos))
                } else if (ch in 'a'..'z') { // funções
                    while (ch in 'a'..'z') nextChar()
                    val func = str.substring(startPos, this.pos)
                    x = parseFactor()
                    if (func == "sqrt")
                        x = Math.sqrt(x)
                    else if (func == "sin")
                        x = Math.sin(Math.toRadians(x))
                    else if (func == "cos")
                        x = Math.cos(Math.toRadians(x))
                    else if (func == "tan")
                        x = Math.tan(Math.toRadians(x))
                    else
                        throw RuntimeException("Função desconhecida: " + func)
                } else {
                    throw RuntimeException("Caractere inesperado: " + ch.toChar())
                }
                if (eat('^')) x = Math.pow(x, parseFactor()) // potência
                return x
            }
        }.parse()
    }
}