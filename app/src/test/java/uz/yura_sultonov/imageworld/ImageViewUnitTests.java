package uz.yura_sultonov.imageworld;

import android.content.Context;
import android.graphics.Bitmap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.File;
import java.io.IOException;

import uz.yura_sultonov.imageworld.activities.FullScreenActivity;
import uz.yura_sultonov.imageworld.utils.Constants;
import uz.yura_sultonov.imageworld.utils.Utilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class ImageViewUnitTests {
    private final Context context = RuntimeEnvironment.application;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getCacheDir_shouldCreateDirectory() throws Exception {
        assertThat(context.getCacheDir()).exists();
    }

    @Test
    public void getExternalCacheDir_shouldCreateDirectory() throws Exception {
        assertThat(context.getExternalCacheDir()).exists();
    }

    @Test
    public void shouldSaveCacheFolder() throws IOException {
        FullScreenActivity activity = new FullScreenActivity();
        Bitmap bitmap = Bitmap.createBitmap(100, 200, Bitmap.Config.ARGB_8888);
        File file = new File(context.getExternalCacheDir(), "ImageWorldPhoto.png");
        if (file.exists())
            file.delete();
        file = new File(context.getExternalCacheDir(), "ImageWorldPhoto.png");
        assertEquals(file.exists(), false);
        Utilities.object().writeToCache(file, bitmap);
        file = new File(context.getExternalCacheDir(), "ImageWorldPhoto.png");
        assertEquals(file.exists(), true);
    }

    @Test
    public void getFilesDir_shouldCreateDirectory() throws Exception {
        assertThat(context.getFilesDir()).exists();
    }

    @Test
    public void fileList() throws Exception {
        assertThat(context.fileList()).isEqualTo(context.getFilesDir().list());
    }

    @Test
    public void getExternalFilesDir_shouldCreateDirectory() throws Exception {
        assertThat(context.getExternalFilesDir(null)).exists();
    }

    @Test
    public void getExternalFilesDir_shouldCreateNamedDirectory() throws Exception {
        File f = context.getExternalFilesDir("__test__");
        assertThat(f).exists();
        assertThat(f.getAbsolutePath()).endsWith("__test__");
    }

    @Test
    public void checkPermissionGrantedFunction() {
        assertThat(Utilities.object().isPermissionGranted(context, Constants.CODE_PERMISSION_READ_STORAGE)).isExactlyInstanceOf(Boolean.class);
    }

    @Test
    public void checkIsNetAvailableFunction() {
        assertThat(Utilities.object().isNetAvailable(context)).isExactlyInstanceOf(Boolean.class);
    }
}