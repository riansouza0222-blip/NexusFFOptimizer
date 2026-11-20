package com.codenexus.nexusff

import android.app.Activity
import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var memory: TextView
    private lateinit var battery: TextView
    private lateinit var thermal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildScreen()
        refreshStats()
    }

    private fun buildScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 28, 28, 24)
            setBackgroundColor(0xFF101820.toInt())
        }
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
        }
        val title = TextView(this).apply {
            text = "NEXUS FF OPTIMIZER"
            textSize = 24f
            setTextColor(0xFF00BFA5.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 18, 0, 8)
        }
        val subtitle = label("Preparação leve para jogar no Xiaomi", 14f, 0xFFCFD8DC.toInt())
        status = label("Status: pronto", 16f, 0xFFFFFFFF.toInt())
        memory = label("Memória: lendo…", 15f, 0xFFFFFFFF.toInt())
        battery = label("Bateria: lendo…", 15f, 0xFFFFFFFF.toInt())
        thermal = label("Temperatura: não disponível", 15f, 0xFFFFFFFF.toInt())

        content.addView(title)
        content.addView(subtitle)
        content.addView(spacer(22))
        content.addView(panel(status))
        content.addView(panel(memory))
        content.addView(panel(battery))
        content.addView(panel(thermal))
        content.addView(spacer(18))
        content.addView(actionButton("Preparar celular", 0xFF00796B.toInt()) { preparePhone() })
        content.addView(actionButton("Ativar Não Perturbe", 0xFF263238.toInt()) { openDndSettings() })
        content.addView(actionButton("Abrir Free Fire", 0xFF00796B.toInt()) { launchGame() })
        content.addView(actionButton("Atualizar informações", 0xFF263238.toInt()) { refreshStats() })
        content.addView(spacer(16))
        content.addView(label("O app não encerra processos, não altera arquivos do jogo e não força configurações ocultas. O Android/MIUI controla a memória e o desempenho.", 12f, 0xFFB0BEC5.toInt()))
        scroll.addView(content)
        root.addView(scroll)
        setContentView(root)
    }

    private fun label(textValue: String, size: Float, color: Int) = TextView(this).apply {
        text = textValue
        textSize = size
        setTextColor(color)
        setPadding(4, 8, 4, 8)
    }

    private fun panel(view: View) = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(14, 8, 14, 8)
        setBackgroundColor(0xFF1B2A33.toInt())
        addView(view)
        val params = LinearLayout.LayoutParams(-1, -2)
        params.setMargins(0, 5, 0, 5)
        layoutParams = params
    }

    private fun spacer(height: Int) = View(this).apply { layoutParams = LinearLayout.LayoutParams(1, height) }

    private fun actionButton(textValue: String, color: Int, action: () -> Unit) = Button(this).apply {
        text = textValue
        isAllCaps = false
        setTextColor(0xFFFFFFFF.toInt())
        setBackgroundColor(color)
        setOnClickListener { action() }
        val params = LinearLayout.LayoutParams(-1, -2)
        params.setMargins(0, 6, 0, 6)
        layoutParams = params
    }

    private fun preparePhone() {
        refreshStats()
        status.text = "Status: informações atualizadas. Ative Não Perturbe se desejar."
        Toast.makeText(this, "Preparação concluída. Nenhum processo foi encerrado.", Toast.LENGTH_LONG).show()
    }

    private fun refreshStats() {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = ActivityManager.MemoryInfo()
        am.getMemoryInfo(info)
        val freeMb = info.availMem / (1024 * 1024)
        val totalMb = info.totalMem / (1024 * 1024)
        memory.text = "Memória disponível: ${freeMb} MB / ${totalMb} MB"

        val batteryIntent = registerReceiver(null, android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        if (batteryIntent != null) {
            val level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val temp = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
            val pct = if (level >= 0 && scale > 0) level * 100 / scale else -1
            battery.text = if (pct >= 0) "Bateria: $pct%" else "Bateria: indisponível"
            thermal.text = if (temp >= 0) "Temperatura da bateria: ${temp / 10.0} °C" else "Temperatura: indisponível"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            val state = when (pm.currentThermalStatus) {
                PowerManager.THERMAL_STATUS_NONE -> "normal"
                PowerManager.THERMAL_STATUS_LIGHT -> "leve"
                PowerManager.THERMAL_STATUS_MODERATE -> "moderada"
                PowerManager.THERMAL_STATUS_SEVERE -> "alta"
                PowerManager.THERMAL_STATUS_CRITICAL -> "crítica"
                PowerManager.THERMAL_STATUS_EMERGENCY -> "emergência"
                else -> "desconhecida"
            }
            thermal.text = "Estado térmico do sistema: $state"
        }
    }

    private fun openDndSettings() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !nm.isNotificationPolicyAccessGranted) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
            Toast.makeText(this, "Permita o acesso para configurar Não Perturbe.", Toast.LENGTH_LONG).show()
        } else {
            startActivity(Intent(Settings.ACTION_SOUND_SETTINGS))
        }
    }

    private fun launchGame() {
        val packages = listOf("com.dts.freefireth", "com.dts.freefiremax")
        for (pkg in packages) {
            val launch = packageManager.getLaunchIntentForPackage(pkg)
            if (launch != null) {
                startActivity(launch)
                return
            }
        }
        Toast.makeText(this, "Free Fire não encontrado. Instale a versão desejada primeiro.", Toast.LENGTH_LONG).show()
    }
}
