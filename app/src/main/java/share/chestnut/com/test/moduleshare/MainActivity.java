package share.chestnut.com.test.moduleshare;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.chestnut.Common.utils.ImageUtils;
import com.chestnut.Common.utils.LogUtils;
import com.chestnut.Share.WX.WXHelper;

public class MainActivity extends AppCompatActivity {

    private WXHelper wxHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wxHelper = new WXHelper(this,"wx8ada24f0c562c9e2");
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = ImageUtils.getBitmap(MainActivity.this.getResources(),R.mipmap.ic_launcher);
                LogUtils.w("MainActivity",wxHelper.sharePic(bitmap,10,2,WXHelper.DIALOG)+"");
            }
        });
    }


}
