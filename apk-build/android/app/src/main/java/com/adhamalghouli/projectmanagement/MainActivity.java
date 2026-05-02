package com.adhamalghouli.projectmanagement;

import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // تسجيل JavaScript Interface لاستدعاء طباعة النظام
        WebView webView = getBridge().getWebView();
        webView.addJavascriptInterface(new PrintBridge(webView, this), "AndroidPrint");
    }

    private static class PrintBridge {
        private final WebView webView;
        private final Context context;

        PrintBridge(WebView webView, Context context) {
            this.webView = webView;
            this.context = context;
        }

        @JavascriptInterface
        public void print(final String jobName) {
            ((android.app.Activity) context).runOnUiThread(() -> {
                try {
                    PrintManager printManager =
                        (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
                    String name = (jobName != null && !jobName.isEmpty())
                        ? jobName : "تقرير المشاريع";
                    PrintDocumentAdapter adapter =
                        webView.createPrintDocumentAdapter(name);
                    printManager.print(name, adapter,
                        new PrintAttributes.Builder().build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        @JavascriptInterface
        public boolean isAvailable() {
            return true;
        }
    }
}
