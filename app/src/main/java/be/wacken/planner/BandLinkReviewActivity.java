package be.wacken.planner;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import java.util.HashMap;
import java.util.Map;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.wacken.planner.application.BandLinkCandidate;
import be.wacken.planner.application.BandLinkResult;
import be.wacken.planner.application.LinkImportedBandUseCase;
import be.wacken.planner.application.ReviewImportedBandLinksUseCase;

public final class BandLinkReviewActivity extends Activity {
    private static final String NO_MATCH = "No match";

    private final Map<String,String> searchTerms=new HashMap<>(),selectedMatches=new HashMap<>();
    private ScrollView scroll;
    private LinearLayout rows;
    private TextView status;
    private AppRepositories repositories;
    private ReviewImportedBandLinksUseCase reviewBands;
    private LinkImportedBandUseCase linkBand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppearanceController.attach(this);
        repositories = new AppRepositories(this);
        reviewBands = new ReviewImportedBandLinksUseCase(
                repositories.festivals(),
                repositories.festivalLineups(),
                repositories.bands()
        );
        linkBand = new LinkImportedBandUseCase(
                repositories.festivals(),
                repositories.festivalLineups(),
                repositories.bands()
        );
        if(savedInstanceState!=null){
            Bundle terms=savedInstanceState.getBundle("terms"),choices=savedInstanceState.getBundle("choices");
            if(terms!=null)for(String key:terms.keySet())searchTerms.put(key,terms.getString(key));
            if(choices!=null)for(String key:choices.keySet())selectedMatches.put(key,choices.getString(key));
        }
        setContentView(content());
        renderRows();
        if(savedInstanceState!=null)scroll.post(()->scroll.scrollTo(0,savedInstanceState.getInt("scroll")));
    }

    @Override protected void onSaveInstanceState(Bundle state){
        Bundle terms=new Bundle(),choices=new Bundle();
        searchTerms.forEach(terms::putString);selectedMatches.forEach(choices::putString);
        state.putBundle("terms",terms);state.putBundle("choices",choices);state.putInt("scroll",scroll.getScrollY());
        super.onSaveInstanceState(state);
    }

    private ScrollView content() {
        scroll = new ScrollView(this);
        WackenTheme.backgroundColor(scroll, WackenTheme.BACKGROUND);

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setPadding(dp(16), dp(16), dp(16), dp(16));
        scroll.addView(screen);

        TextView title = new TextView(this);
        title.setText("Link Imported Bands");
        WackenTheme.textColor(title, WackenTheme.AMBER);
        title.setTextSize(26);
        title.setGravity(Gravity.START);
        WackenTheme.heading(title);
        screen.addView(title);

        status = new TextView(this);
        status.setTextSize(14);status.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        WackenTheme.textColor(status, WackenTheme.MUTED);
        status.setPadding(0, dp(8), 0, dp(12));
        status.setText("Review possible matches. No match is valid.");
        screen.addView(status);

        rows = new LinearLayout(this);
        rows.setOrientation(LinearLayout.VERTICAL);
        screen.addView(rows);

        screen.addView(actionButton("Refresh matches", view -> renderRows()));
        screen.addView(actionButton("Back to settings", view -> finish()));
        return scroll;
    }

    private void renderRows() {
        rows.removeAllViews();
        List<BandLinkCandidate> candidates = reviewBands.review();
        if (candidates.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No likely imported-band matches found.");
            WackenTheme.textColor(empty, WackenTheme.TEXT);
            empty.setPadding(dp(12), dp(10), dp(12), dp(10));
            empty.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 6));
            rows.addView(empty);
            return;
        }
        for (BandLinkCandidate candidate : candidates) {
            String key=rowKey(candidate);
            String term=searchTerms.getOrDefault(key,candidate.searchTerm());
            rows.addView(candidateRow(reviewBands.search(candidate.uploadedDisplayName(),candidate.currentBandName(),term)));
        }
    }

    private LinearLayout candidateRow(BandLinkCandidate candidate) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(dp(12), dp(10), dp(12), dp(12));
        row.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 6));
        LinearLayout.LayoutParams rowLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rowLayout.setMargins(0, 0, 0, dp(10));
        row.setLayoutParams(rowLayout);

        TextView imported = new TextView(this);
        imported.setText(candidate.uploadedDisplayName());
        WackenTheme.textColor(imported, WackenTheme.TEXT);
        imported.setTextSize(16);
        LinearLayout.LayoutParams importedLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        importedLayout.setMargins(0, 0, dp(10), 0);
        imported.setLayoutParams(importedLayout);
        row.addView(imported);

        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.VERTICAL);
        controls.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        String key=rowKey(candidate);
        EditText search = new EditText(this);
        search.setSaveEnabled(false);
        search.setContentDescription("Search own bands for "+candidate.uploadedDisplayName());
        search.setText(candidate.searchTerm());
        search.setSingleLine(true);
        search.setHint("Search own band database");
        WackenTheme.textColor(search, WackenTheme.TEXT);
        WackenTheme.reviewInput(search);
        controls.addView(search);

        Spinner matches = new Spinner(this);
        matches.setSaveEnabled(false);matches.setMinimumHeight(dp(48));
        matches.setContentDescription("Match for "+candidate.uploadedDisplayName());WackenTheme.focusablePanel(matches);
        setMatches(matches,candidate.candidateBandNames(),selectedMatches.get(key));
        matches.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent,View view,int position,long id){selectedMatches.put(key,parent.getItemAtPosition(position).toString());}
            public void onNothingSelected(AdapterView<?> parent){}
        });
        controls.addView(matches);

        Button link = actionButton("Link selected match", view -> {
            String selected = matches.getSelectedItem() == null ? NO_MATCH : matches.getSelectedItem().toString();
            BandLinkResult result = linkBand.link(
                    candidate.currentBandName(),
                    candidate.uploadedDisplayName(),
                    NO_MATCH.equals(selected) ? "" : selected
            );
            WackenTheme.textColor(status,result.success() ? WackenTheme.SUCCESS_GREEN : WackenTheme.RED);
            status.setText(result.message());
            renderRows();
        });
        controls.addView(link);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable text) {
                BandLinkCandidate updated = reviewBands.search(
                        candidate.uploadedDisplayName(),
                        candidate.currentBandName(),
                        text.toString()
                );
                searchTerms.put(key,text.toString());selectedMatches.put(key,NO_MATCH);
                setMatches(matches,updated.candidateBandNames(),NO_MATCH);
            }
        });

        row.addView(controls);
        return row;
    }

    private String rowKey(BandLinkCandidate candidate){return candidate.currentBandName()+"\u0000"+candidate.uploadedDisplayName();}

    private void setMatches(Spinner matches,List<String> candidateNames,String selected) {
        List<String> values=new ArrayList<>();values.add(NO_MATCH);values.addAll(candidateNames);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,values){
            @Override public View getView(int position,View reusable,ViewGroup parent){return option(super.getView(position,reusable,parent));}
            @Override public View getDropDownView(int position,View reusable,ViewGroup parent){return option(super.getDropDownView(position,reusable,parent));}
            private View option(View view){
                TextView text=(TextView)view;text.setTextSize(16);text.setSingleLine(false);text.setEllipsize(null);
                text.setMinHeight(dp(48));text.setPadding(dp(8),dp(8),dp(8),dp(8));
                WackenTheme.textColor(text,WackenTheme.TEXT);WackenTheme.backgroundColor(text,WackenTheme.PANEL);return text;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);matches.setAdapter(adapter);
        int position=selected==null?(values.size()>1?1:0):Math.max(0,values.indexOf(selected));
        matches.setSelection(position);
    }

    private Button actionButton(String text, android.view.View.OnClickListener listener) {
        Button button = WackenTheme.actionButton(this, text, WackenTheme.ButtonStyle.SECONDARY, listener);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setMargins(0, dp(8), 0, 0);
        button.setLayoutParams(layout);
        return button;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
