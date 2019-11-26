package eu.ioannidis.speedometer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import eu.ioannidis.speedometer.adapters.ViolationListAdapter;
import eu.ioannidis.speedometer.config.DatabaseConfig;
import eu.ioannidis.speedometer.models.ViolationModel;

public class ViolationsActivity extends AppCompatActivity {

    private static final String TAG = "ViolationsActivity";

    private List<ViolationModel> items;

    private DatabaseConfig dbHandler = new DatabaseConfig(this, null, null, 1);


    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_violations);
        setContentView(R.layout.activity_violations);

        // Enable back button on actionbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        // Get all violations from database
        items = dbHandler.getViolations();


        recyclerView = (RecyclerView) findViewById(R.id.violation_list);
        recyclerView.setHasFixedSize(true);


        // Recycle view adapter
        mAdapter = new ViolationListAdapter(items, this);
        recyclerView.setAdapter(mAdapter);
        // User linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Add functionality to back button
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
