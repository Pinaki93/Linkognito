package dev.pinaki.linkognito;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ShareActivity extends AppCompatActivity {
    private static final String TAG = "ShareActivity";

    // Common tracking parameters to remove
    private static final Set<String> TRACKING_PARAMETERS = new HashSet<>() {{
        add("utm_source");
        add("utm_medium");
        add("utm_campaign");
        add("utm_term");
        add("utm_content");
        add("fbclid");
        add("gclid");
        add("msclkid");
        add("ref");
        add("source");
        add("campaign");
        add("medium");
        add("term");
        add("content");
        add("si");
        add("s");
        add("spm");
        add("scm");
        add("from");
        add("via");
        add("referrer");
        add("referral");
        add("affiliate");
        add("partner");
        add("click_id");
        add("clickid");
        add("click");
        add("tracking");
        add("track");
        add("analytics");
        add("stats");
        add("statistics");
        add("data");
        add("info");
        add("details");
        add("params");
        add("parameters");
        add("vars");
        add("variables");
        add("custom");
        add("user");
        add("session");
        add("visit");
        add("visitor");
        add("client");
        add("device");
        add("platform");
        add("os");
        add("browser");
        add("app");
        add("version");
        add("build");
        add("release");
        add("channel");
        add("distribution");
        add("store");
        add("market");
        add("shop");
        add("retailer");
        add("merchant");
        add("vendor");
        add("seller");
        add("buyer");
        add("customer");
        add("user_id");
        add("userid");
        add("uid");
        add("id");
        add("email");
        add("phone");
        add("mobile");
        add("tel");
        add("address");
        add("location");
        add("geo");
        add("country");
        add("region");
        add("city");
        add("zip");
        add("postal");
        add("state");
        add("province");
        add("area");
        add("zone");
        add("timezone");
        add("time");
        add("date");
        add("timestamp");
        add("epoch");
        add("unix");
        add("jwt");
        add("token");
        add("auth");
        add("key");
        add("secret");
        add("password");
        add("pwd");
        add("hash");
        add("signature");
        add("sign");
        add("verify");
        add("validate");
        add("check");
        add("test");
        add("debug");
        add("dev");
        add("development");
        add("staging");
        add("beta");
        add("alpha");
        add("preview");
        add("demo");
        add("sample");
        add("example");
        add("mock");
        add("fake");
        add("dummy");
        add("placeholder");
        add("temp");
        add("temporary");
        add("cache");
        add("cached");
        add("stored");
        add("saved");
        add("backup");
        add("archive");
        add("old");
        add("new");
        add("updated");
        add("modified");
        add("changed");
        add("edited");
        add("created");
        add("added");
        add("removed");
        add("deleted");
        add("cleaned");
        add("purged");
        add("expired");
        add("invalid");
        add("error");
        add("failed");
        add("success");
        add("complete");
        add("finished");
        add("done");
        add("ready");
        add("active");
        add("inactive");
        add("enabled");
        add("disabled");
        add("on");
        add("off");
        add("true");
        add("false");
        add("yes");
        add("no");
        add("1");
        add("0");
        add("igsh");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity transparent
        setTheme(R.style.Theme_Linkognito_Transparent);

        // Handle the incoming intent
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            Log.e(TAG, "Intent is null");
            finish();
            return;
        }

        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    processAndShareUrl(sharedText);
                } else {
                    Log.e(TAG, "Shared text is null");
                    finish();
                }
            }
        } else if (Intent.ACTION_VIEW.equals(action)) {
            Uri data = intent.getData();
            if (data != null) {
                processAndShareUrl(data.toString());
            } else {
                Log.e(TAG, "Data URI is null");
                finish();
            }
        } else {
            Log.e(TAG, "Unsupported action: " + action);
            finish();
        }
    }

    private void processAndShareUrl(String url) {
        try {
            String cleanedUrl = removeTrackingParameters(url);
            Log.d(TAG, "Original URL: " + url);
            Log.d(TAG, "Cleaned URL: " + cleanedUrl);

            // Create share intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, cleanedUrl);

            // Show share dialog
            Intent chooser = Intent.createChooser(shareIntent, getString(R.string.share_clean_url_title));
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(chooser);

        } catch (Exception e) {
            Log.e(TAG, "Error processing URL: " + e.getMessage());
        } finally {
            // Finish the activity after sharing
            finish();
        }
    }

    private String removeTrackingParameters(String url) {
        try {
            URL urlObj = new URL(url);
            String protocol = urlObj.getProtocol();
            String host = urlObj.getHost();
            int port = urlObj.getPort();
            String path = urlObj.getPath();
            String fragment = urlObj.getRef();

            // Parse query parameters
            String query = urlObj.getQuery();
            if (query == null || query.isEmpty()) {
                return url; // No parameters to clean
            }

            StringBuilder cleanedQuery = new StringBuilder();
            String[] params = query.split("&");
            boolean firstParam = true;

            for (String param : params) {
                String[] keyValue = param.split("=", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].toLowerCase();
                    String value = keyValue[1];

                    // Check if this is a tracking parameter
                    if (permitParameter(key)) {
                        if (!firstParam) {
                            cleanedQuery.append("&");
                        }
                        cleanedQuery.append(keyValue[0]).append("=").append(value);
                        firstParam = false;
                    }
                } else if (keyValue.length == 1 && !keyValue[0].isEmpty()) {
                    // Parameter without value
                    String key = keyValue[0].toLowerCase();
                    if (permitParameter(key)) {
                        if (!firstParam) {
                            cleanedQuery.append("&");
                        }
                        cleanedQuery.append(keyValue[0]);
                        firstParam = false;
                    }
                }
            }

            // Reconstruct the URL
            StringBuilder cleanedUrl = new StringBuilder();
            cleanedUrl.append(protocol).append("://").append(host);

            if (port != -1) {
                cleanedUrl.append(":").append(port);
            }

            cleanedUrl.append(path);

            if (cleanedQuery.length() > 0) {
                cleanedUrl.append("?").append(cleanedQuery);
            }

            if (fragment != null) {
                cleanedUrl.append("#").append(fragment);
            }

            return cleanedUrl.toString();

        } catch (Exception e) {
            Log.e(TAG, "Error parsing URL: " + e.getMessage());
            return url; // Return original URL if parsing fails
        }
    }

    private boolean permitParameter(String key) {
        // Check exact matches
        return !TRACKING_PARAMETERS.contains(key);
    }
} 