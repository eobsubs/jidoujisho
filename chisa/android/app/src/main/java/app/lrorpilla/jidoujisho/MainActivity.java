// Derived from the AnkiDroid API Sample

package app.arianneorpilla.jidoujisho;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.net.Uri;

import androidx.annotation.NonNull;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

import com.ryanheise.audioservice.AudioServicePlugin;
import com.ichi2.anki.api.AddContentApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends FlutterActivity {
    private static final String ANKIDROID_CHANNEL = "com.arianneorpilla.api/ankidroid";

    private static final int AD_PERM_REQUEST = 0;

    private Activity context;
    private AnkiDroidHelper mAnkiDroid;

    @Override
    public FlutterEngine provideFlutterEngine(Context context) {
        return AudioServicePlugin.getFlutterEngine(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = MainActivity.this;
        // Create the example data
        mAnkiDroid = new AnkiDroidHelper(context);
    }

    private void addNote(String deck, String sentence, String word, String reading, String meaning,  String image, String audio, String extra, String contextParam) {
        final AddContentApi api = new AddContentApi(context);

        long deckId;
        if (deckExists(deck)) {
            deckId = mAnkiDroid.findDeckIdByName(deck);
        } else {
            deckId = api.addNewDeck(deck);
        }

        long modelId;
        if (modelExists("jidoujisho Chisa")) {
            modelId = mAnkiDroid.findModelIdByName("jidoujisho Chisa", 8);
        } else {
            modelId = api.addNewCustomModel("jidoujisho Chisa",
                new String[] {
                    "Sentence",
                    "Word",
                    "Reading",
                    "Meaning",
                    "Image",
                    "Audio",
                    "Extra",
                    "Context",
                },
                new String[] {
                    "jidoujisho Chisa Default"
                },
                new String[] {"<p id=\"sentence\">{{Sentence}}</p><div id=\"word\">{{Word}}</div>"},
                    new String[] {"<p id=\"sentence\">{{Sentence}}</p><div id=\"word\">{{Word}}</div><br>{{Audio}}<div class=\"image\">{{Image}}</div><hr id=reading><p id=\"reading\">{{Reading}}</p><h2 id=\"word\">{{Word}}</h2><br><p><small id=\"meaning\">{{Meaning}}</small></p><br>{{#Context}}<a style=\"text-decoration:none;color:red;\" href=\"{{Context}}\">↩</a>{{/Context}}"},
                            "p {\n" +
                            "    margin: 0px\n" +
                            "}\n" +
                            "\n" +
                            "h2 {\n" +
                            "    margin: 0px\n" +
                            "}\n" +
                            "\n" +
                            "small {\n" +
                            "    margin: 0px\n" +
                            "}\n" +
                            "\n" +
                            ".card {\n" +
                            "  font-family: arial;\n" +
                            "  font-size: 20px;\n" +
                            "  text-align: center;\n" +
                            "  color: black;\n" +
                            "  background-color: white;\n" +
                            "  white-space: pre-line;\n" +
                            "}\n" +
                            "\n" +
                            "#sentence {\n" +
                            "    font-size: 30px\n" +
                            "}\n" +
                            "\n" +
                            ".context.night_mode {\n" + 
                            "    text-decoration: none;\n" +
                            "    color: red;\n" +
                            "}\n" +
                            ".context {\n" +
                            "    text-decoration: none;\n" +
                            "    color: red;\n" +
                            "}\n" +
                            "\n" +
                            ".image img {\n" +
                            "  position: static;\n" +
                            "  height: auto;\n" +
                            "  width: auto;\n" +
                            "  max-height: 250px;\n" +
                            "}\n" +
                            ".pitch{\n" +
                            "  border-top: solid red 2px;\n" +
                            "  padding-top: 1px;\n" +
                            "}\n" +
                            "\n" +
                            ".pitch_end{\n" +
                            "  border-color: red;\n" +
                            "  border-right: solid red 2px;\n" +
                            "  border-top: solid red 2px;  \n" +
                            "  line-height: 1px;\n" +
                            "  margin-right: 1px;\n" +
                            "  padding-right: 1px;\n" +
                            "  padding-top:1px;\n" +
                            "}",
                    null,
                    null
                    );
        }

        Set<String> tags = new HashSet<>(Arrays.asList("Chisa"));

        api.addNote(modelId, deckId, new String[] {
            sentence,
            word,
            reading,
            meaning,
            image,
            audio,
            extra,
            contextParam,
        }, tags);

        System.out.println("Added note via flutter_ankidroid_api");
        System.out.println("Model: " + modelId);
        System.out.println("Deck: " + deckId);
    }

    private boolean deckExists(String deck) {
        Long deckId = mAnkiDroid.findDeckIdByName(deck);
        return (deckId != null);
    }

    private boolean modelExists(String model) {
        Long deckId = mAnkiDroid.findModelIdByName(model, 8);
        return (deckId != null);
    }

    private Long getDeckId() {
        Long did = mAnkiDroid.findDeckIdByName(AnkiDroidConfig.DECK_NAME);
        if (did == null) {
            did = mAnkiDroid.getApi().addNewDeck(AnkiDroidConfig.DECK_NAME);
            mAnkiDroid.storeDeckReference(AnkiDroidConfig.DECK_NAME, did);
        }
        return did;
    }

    private Long getModelId() {
        Long mid = mAnkiDroid.findModelIdByName(AnkiDroidConfig.MODEL_NAME, AnkiDroidConfig.FIELDS.length);
        if (mid == null) {
            mid = mAnkiDroid.getApi().addNewCustomModel(AnkiDroidConfig.MODEL_NAME, AnkiDroidConfig.FIELDS,
                AnkiDroidConfig.CARD_NAMES, AnkiDroidConfig.QFMT, AnkiDroidConfig.AFMT, AnkiDroidConfig.CSS, getDeckId(), null);
            mAnkiDroid.storeModelReference(AnkiDroidConfig.MODEL_NAME, mid);
        }
        return mid;
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {

        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), ANKIDROID_CHANNEL)
            .setMethodCallHandler(
                (call, result) -> {
                    final String deck = call.argument("deck");
                    final String sentence = call.argument("sentence");
                    final String word = call.argument("word");
                    final String meaning = call.argument("meaning");
                    final String reading = call.argument("reading");
                    final String image = call.argument("image");
                    final String audio = call.argument("audio");
                    final String extra = call.argument("extra");
                    final String contextParam = call.argument("contextParam");

                    final String fileUriPath = call.argument("fileUriPath");
                    final String preferredName = call.argument("preferredName");
                    final String mimeType = call.argument("mimeType");
                    final AddContentApi api = new AddContentApi(context);

                    switch (call.method) {
                        case "addNote":
                            addNote(deck, sentence, word, reading, meaning, image, audio, extra, contextParam);
                            result.success("Added note");
                            break;
                        case "getDecks":
                            result.success(api.getDeckList());
                            break;
                        case "requestPermissions":
                            if (mAnkiDroid.shouldRequestPermission()) {
                                mAnkiDroid.requestPermission(MainActivity.this, AD_PERM_REQUEST);
                            }
                            break;
                        case "addMediaFromUri":
                            System.out.println(fileUriPath);
                            System.out.println(preferredName);
                            System.out.println(mimeType);
                            Uri fileUri = Uri.parse(fileUriPath);

                            try {
                                String addedFileName = api.addMediaFromUri(fileUri, preferredName, mimeType);
                                result.success(addedFileName);
                                System.out.println("Added media from URI");
                            } catch (Exception e) {
                                System.out.println(e);
                            }


                            break;
                        default:
                            result.notImplemented();
                    }
                }

            );
    }
}