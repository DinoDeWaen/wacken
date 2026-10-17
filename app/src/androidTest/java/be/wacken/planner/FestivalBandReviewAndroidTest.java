package be.wacken.planner;

import android.app.*;
import android.content.Intent;
import android.widget.*;
import androidx.test.platform.app.InstrumentationRegistry;
import be.wacken.planner.application.*;
import be.wacken.planner.domain.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.*;
import org.junit.*;
import static org.junit.Assert.*;

public final class FestivalBandReviewAndroidTest {
    private final Instrumentation ui=InstrumentationRegistry.getInstrumentation();
    private ImportCsvActivity activity;
    private FestivalBandReviewDialog review;
    private final AtomicReference<List<ReviewedFestivalBand>> saved=new AtomicReference<>();
    private final AtomicInteger cancellations=new AtomicInteger();
    private final CanonicalBand canonical=new CanonicalBand("known-id",new Band("Abbie Falls"));
    @Before public void open() {
        activity=(ImportCsvActivity)ui.startActivitySync(new Intent(ui.getTargetContext(),ImportCsvActivity.class)
            .putExtra(ImportCsvActivity.EXTRA_ADD_FESTIVAL_MODE,true).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
    @After public void close() { ui.runOnMainSync(()->{if(review!=null)review.dismiss();activity.finish();}); }
    @Test public void fuzzyIdentityRequiresChoiceAndSeparateFinalConfirmation() {
        show("Abbie Fall",List.of(canonical));
        ui.runOnMainSync(()->{
            assertFalse(dialog().getButton(AlertDialog.BUTTON_POSITIVE).isEnabled());
            assertFalse(((CheckBox)field(review,"remember")).isEnabled());
            ((Spinner)field(review,"selection")).setSelection(2);
        });
        ui.waitForIdleSync();
        ui.runOnMainSync(()->{
            CheckBox remember=(CheckBox)field(review,"remember");
            assertTrue(remember.isEnabled());remember.setChecked(true);
            dialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick();
            assertNull(saved.get());
        });
        ui.waitForIdleSync();
        ui.runOnMainSync(()->dialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick());
        ui.waitForIdleSync();
        assertEquals(canonical,saved.get().get(0).selected().orElseThrow());
        assertTrue(saved.get().get(0).rememberAlias());
    }
    @Test public void cancellationDoesNotConfirmAnyFestivalOrAlias() {
        show("AF",List.of(canonical));
        ui.runOnMainSync(()->dialog().getButton(AlertDialog.BUTTON_NEGATIVE).performClick());
        ui.waitForIdleSync();
        assertNull(saved.get());assertEquals(1,cancellations.get());
    }
    @Test public void explicitNoMatchCreatesNewWithoutRememberingAlias() {
        show("New",List.of());
        ui.runOnMainSync(()->((Spinner)field(review,"selection")).setSelection(1));
        ui.waitForIdleSync();
        ui.runOnMainSync(()->dialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick());
        ui.waitForIdleSync();
        ui.runOnMainSync(()->dialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick());
        ui.waitForIdleSync();
        assertTrue(saved.get().get(0).selected().isEmpty());assertFalse(saved.get().get(0).rememberAlias());
    }
    @Test public void choicesForDifferentRowsRemainIndependentUntilFinalSummary() {
        ui.runOnMainSync(()->{
            review=new FestivalBandReviewDialog(activity,List.of(
                new ReviewFestivalBandsUseCase.Proposal("AF",Optional.empty(),List.of(canonical)),
                new ReviewFestivalBandsUseCase.Proposal("New Act",Optional.empty(),List.of())),saved::set,cancellations::incrementAndGet);
            review.show();
        });
        ui.waitForIdleSync();
        ui.runOnMainSync(()->((Spinner)field(review,"selection")).setSelection(2));ui.waitForIdleSync();
        ui.runOnMainSync(()->{
            ((CheckBox)field(review,"remember")).setChecked(true);
            dialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick();
        });ui.waitForIdleSync();
        ui.runOnMainSync(()->{
            assertEquals(0,((Spinner)field(review,"selection")).getSelectedItemPosition());
            assertFalse(((CheckBox)field(review,"remember")).isChecked());
            assertFalse(dialog().getButton(AlertDialog.BUTTON_POSITIVE).isEnabled());
            ((Spinner)field(review,"selection")).setSelection(1);
        });ui.waitForIdleSync();
        ui.runOnMainSync(()->dialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick());ui.waitForIdleSync();
        assertNull(saved.get());
        ui.runOnMainSync(()->dialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick());ui.waitForIdleSync();
        assertEquals(2,saved.get().size());assertTrue(saved.get().get(0).rememberAlias());
        assertFalse(saved.get().get(1).rememberAlias());assertTrue(saved.get().get(1).selected().isEmpty());
    }

    private void show(String name,List<CanonicalBand> candidates) {
        ui.runOnMainSync(()->{
            review=new FestivalBandReviewDialog(activity,List.of(new ReviewFestivalBandsUseCase.Proposal(name,Optional.empty(),candidates)),
                saved::set,cancellations::incrementAndGet);
            review.show();
        });
        ui.waitForIdleSync();
    }
    private AlertDialog dialog() { return (AlertDialog)field(review,"dialog"); }
    private static Object field(Object target,String name) {
        try {Field f=target.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(target);}
        catch(Exception error) {throw new AssertionError(error);}
    }
}
