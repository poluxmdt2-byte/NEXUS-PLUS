package com.nexusplus.app;

import androidx.activity.ComponentActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class MainActivity extends ComponentActivity {
    private final int navy = Color.rgb(8, 13, 28);
    private final int panel = Color.rgb(17, 29, 52);
    private final int blue = Color.rgb(86, 185, 255);
    private ExoPlayer player;
    private ActivityResultLauncher<Intent> videoPicker;
    private String selectedEpisode = "WandaVision";

    private final String[] titles = {
        "Gravado ao Vivo com Plateia", "Não Mude de Canal", "Agora em Cores",
        "Interrompemos Este Programa", "Em um Episódio Muito Especial...",
        "Um Halloween Assustadoramente Inédito!", "Derrubando a Quarta Parede",
        "Nos Capítulos Anteriores", "O Grande Final"
    };
    private final String[] descriptions = {
        "Wanda e Vision tentam esconder seus poderes durante um jantar com o chefe de Vision.",
        "O casal se prepara para apresentar um número de mágica no show de talentos da comunidade.",
        "A gravidez de Wanda e seus poderes avançam de forma inesperada.",
        "Monica Rambeau participa de uma missão especial que revela o mistério de Westview.",
        "Vision começa a desconfiar do comportamento dos vizinhos enquanto Wanda enfrenta novas perguntas.",
        "O Halloween traz acontecimentos inesperados e Vision decide investigar a cidade.",
        "Monica planeja voltar, Wanda enfrenta complicações e Vision encontra uma possível aliada.",
        "Wanda revisita momentos importantes de sua história.",
        "Os acontecimentos chegam ao confronto decisivo e ao destino dos moradores de Westview."
    };

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(navy);
        getWindow().setNavigationBarColor(navy);
        videoPicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::onVideoSelected
        );
        showCatalog();
    }

    private void showCatalog() {
        releasePlayer();
        LinearLayout content = new LinearLayout(this);
        content.setPadding(dp(20), dp(24), dp(20), dp(32));
        content.setOrientation(LinearLayout.VERTICAL);
        content.setBackgroundColor(navy);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.addView(content);

        content.addView(text("NEXUS+", 30, blue, true));
        content.addView(text("SEU UNIVERSO MARVEL", 12, Color.LTGRAY, true));

        TextView hero = text("WANDAVISION", 28, Color.WHITE, true);
        hero.setPadding(0, dp(34), 0, dp(6));
        content.addView(hero);
        content.addView(text("2021  •  1 temporada  •  9 episódios", 13, blue, false));

        TextView synopsis = text(
            "Wanda Maximoff e Vision vivem uma vida aparentemente perfeita em uma cidade suburbana. "
            + "Entre estilos de sitcom e acontecimentos estranhos, a realidade começa a mostrar suas falhas.",
            15, Color.LTGRAY, false
        );
        synopsis.setPadding(0, dp(14), 0, dp(18));
        content.addView(synopsis);

        content.addView(text("REPRODUZA SEUS VÍDEOS LOCAIS", 12, blue, true));
        TextView help = text("Toque em um episódio e escolha o arquivo de vídeo salvo no seu aparelho.", 13, Color.LTGRAY, false);
        help.setPadding(0, dp(6), 0, dp(18));
        content.addView(help);

        for (int i = 0; i < titles.length; i++) {
            final int index = i;
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(14), dp(12), dp(14), dp(12));
            card.setBackground(round(panel, 14));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            params.bottomMargin = dp(10);
            content.addView(card, params);

            card.addView(text(String.format("%02d  •  %s", i + 1, titles[i]), 15, Color.WHITE, true));
            TextView desc = text(descriptions[i], 12, Color.LTGRAY, false);
            desc.setPadding(0, dp(6), 0, dp(10));
            card.addView(desc);
            Button play = button("ESCOLHER VÍDEO");
            play.setOnClickListener(v -> pickVideo(index));
            card.addView(play);
        }
        setContentView(scroll);
    }

    private void pickVideo(int index) {
        selectedEpisode = String.format("Episódio %02d — %s", index + 1, titles[index]);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        videoPicker.launch(intent);
    }

    private void onVideoSelected(ActivityResult result) {
        if (result.getResultCode() != RESULT_OK || result.getData() == null) return;
        Uri uri = result.getData().getData();
        if (uri == null) return;
        try {
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (Exception ignored) { }
        showPlayer(uri);
    }

    private void showPlayer(Uri uri) {
        releasePlayer();
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(16), dp(20), dp(16), dp(16));
        layout.setBackgroundColor(navy);

        Button back = button("←  VOLTAR AO CATÁLOGO");
        back.setOnClickListener(v -> showCatalog());
        layout.addView(back);

        TextView heading = text(selectedEpisode, 18, Color.WHITE, true);
        heading.setPadding(0, dp(22), 0, dp(12));
        layout.addView(heading);

        PlayerView playerView = new PlayerView(this);
        layout.addView(playerView, new LinearLayout.LayoutParams(-1, dp(240)));
        setContentView(layout);

        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        player.setMediaItem(MediaItem.fromUri(uri));
        player.prepare();
        player.play();
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.WHITE);
        b.setTextSize(12);
        b.setAllCaps(false);
        b.setBackground(round(Color.rgb(18, 59, 112), 10));
        return b;
    }

    private GradientDrawable round(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override protected void onStop() {
        super.onStop();
        if (player != null) player.pause();
    }

    @Override protected void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }
}
