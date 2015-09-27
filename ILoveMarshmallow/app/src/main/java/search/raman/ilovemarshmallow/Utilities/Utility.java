package search.raman.ilovemarshmallow.Utilities;

import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.view.Display;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.xml.sax.XMLReader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import search.raman.ilovemarshmallow.R;

/********************************************************************************************************************************
 * Developer : Ramanpreet Singh Khinda
 * <p/>
 * This class is Utility class for providing utility features like reusable methods to other classes
 ********************************************************************************************************************************/
public class Utility {

    public Utility() {
        super();
    }

    public int getScreenWidth(Display display) {
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public int nthLastIndexOf(String str, char c, int n) {
        if (str == null || n < 1)
            return -1;
        int pos = str.length();
        while (n-- > 0 && pos != -1)
            pos = str.lastIndexOf(c, pos - 1);
        return pos;
    }

    public boolean isNetworkAvailable(final Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public boolean isValidEmail(String email) {
        boolean isValidEmail = false;

        String emailExpression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(emailExpression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {
            isValidEmail = true;
        }

        return isValidEmail;
    }

    public String getAndroidID(Context context) {
        if (context == null)
            return "";

        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return android_id;
    }

    public class MyTagHandler implements Html.TagHandler {
        boolean first = true;
        String parent = null;
        int index = 1;

        @Override
        public void handleTag(boolean opening, String tag, Editable output,
                              XMLReader xmlReader) {

            if (tag.equals("ul")) parent = "ul";
            else if (tag.equals("ol")) parent = "ol";
            if (tag.equals("li")) {
                if (parent.equals("ul")) {
                    if (first) {
                        output.append("\n\u2022 ");

                        first = false;
                    } else {
                        first = true;
                    }
                } else {
                    if (first) {
                        output.append("\t" + index + "• ");
                        first = false;
                        index++;
                    } else {
                        first = true;
                    }
                }
            }
        }
    }
}
