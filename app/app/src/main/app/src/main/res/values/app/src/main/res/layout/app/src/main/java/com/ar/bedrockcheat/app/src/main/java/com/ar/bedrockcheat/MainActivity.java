package com.ar.bedrockcheat;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import com.ar.bedrockcheat.manager.ServerManager;

public class MainActivity extends Activity {
    private EditText etServerIP, etServerPort, etPlayerName;
    private TextView tvStatus, tvVersion;
    private Switch swFly, swSpeed, swKillAura, swESP, swAntiKB, swGod;
    private Button btnLaunch, btnSave, btnTest;
    private ServerManager serverManager;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs         = getSharedPreferences("BCL", MODE_PRIVATE);
        serverManager = new ServerManager();
        initViews();
        loadSettings();
        setupListeners();
        tvVersion.setText("AR Cheat Launcher v1.0");
    }

    private void initViews() {
        etServerIP   = findViewById(R.id.etServerIP);
        etServerPort = findViewById(R.id.etServerPort);
        etPlayerName = findViewById(R.id.etPlayerName);
        tvStatus     = findViewById(R.id.tvStatus);
        tvVersion    = findViewById(R.id.tvVersion);
        swFly        = findViewById(R.id.swFly);
        swSpeed      = findViewById(R.id.swSpeed);
        swKillAura   = findViewById(R.id.swKillAura);
        swESP        = findViewById(R.id.swESP);
        swAntiKB     = findViewById(R.id.swAntiKB);
        swGod        = findViewById(R.id.swGod);
        btnLaunch    = findViewById(R.id.btnLaunch);
        btnSave      = findViewById(R.id.btnSave);
        btnTest      = findViewById(R.id.btnTest);
    }

    private void loadSettings() {
        etServerIP.setText(prefs.getString("ip", ""));
        etServerPort.setText(prefs.getString("port", "19133"));
        etPlayerName.setText(prefs.getString("name", ""));
        swFly.setChecked(prefs.getBoolean("fly", false));
        swSpeed.setChecked(prefs.getBoolean("speed", false));
        swKillAura.setChecked(prefs.getBoolean("aura", false));
        swESP.setChecked(prefs.getBoolean("esp", false));
        swAntiKB.setChecked(prefs.getBoolean("antikb", false));
        swGod.setChecked(prefs.getBoolean("god", false));
    }

    private void saveSettings() {
        prefs.edit()
            .putString("ip",    etServerIP.getText().toString().trim())
            .putString("port",  etServerPort.getText().toString().trim())
            .putString("name",  etPlayerName.getText().toString().trim())
            .putBoolean("fly",    swFly.isChecked())
            .putBoolean("speed",  swSpeed.isChecked())
            .putBoolean("aura",   swKillAura.isChecked())
            .putBoolean("esp",    swESP.isChecked())
            .putBoolean("antikb", swAntiKB.isChecked())
            .putBoolean("god",    swGod.isChecked())
            .apply();
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> {
            saveSettings();
            tvStatus.setText("Gespeichert: " + etServerIP.getText() + ":" + etServerPort.getText());
        });
        btnTest.setOnClickListener(v -> {
            String ip = etServerIP.getText().toString().trim();
            String portStr = etServerPort.getText().toString().trim();
            if (ip.isEmpty()) { tvStatus.setText("Server IP eingeben"); return; }
            int port = portStr.isEmpty() ? 19133 : Integer.parseInt(portStr);
            tvStatus.setText("Verbinde...");
            serverManager.ping(ip, port, ok ->
                runOnUiThread(() -> tvStatus.setText(ok ? "Server erreichbar!" : "Nicht erreichbar.")));
        });
        btnLaunch.setOnClickListener(v -> {
            saveSettings();
            String ip = etServerIP.getText().toString().trim();
            if (ip.isEmpty()) { tvStatus.setText("Server IP eingeben!"); return; }
            StringBuilder cmds = new StringBuilder();
            if (swFly.isChecked())      cmds.append("/cheat fly\n");
            if (swSpeed.isChecked())    cmds.append("/cheat speed\n");
            if (swKillAura.isChecked()) cmds.append("/cheat aura\n");
            if (swESP.isChecked())      cmds.append("/cheat esp\n");
            if (swAntiKB.isChecked())   cmds.append("/cheat antikb\n");
            if (swGod.isChecked())      cmds.append("/cheat god\n");
            ClipboardManager cb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            cb.setPrimaryClip(ClipData.newPlainText("cheats", cmds.toString()));
            tvStatus.setText("Befehle kopiert! Nach Join im Chat einfuegen.");
            Intent mc = getPackageManager().getLaunchIntentForPackage("com.mojang.minecraftpe");
            if (mc != null) { startActivity(mc); }
            else {
                startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.mojang.minecraftpe")));
            }
        });
    }
}
