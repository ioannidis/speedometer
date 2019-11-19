package eu.ioannidis.speedometer.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import eu.ioannidis.speedometer.MainActivity;
import eu.ioannidis.speedometer.R;
import eu.ioannidis.speedometer.ViolationDetailsActivity;
import eu.ioannidis.speedometer.models.ViolationModel;

public class ViolationListAdapter extends RecyclerView.Adapter<ViolationListAdapter.ViewHolder> {

    private static final String TAG = "ViolationListAdapter";

    private List<ViolationModel> items;

    private Context context;

    public ViolationListAdapter(List<ViolationModel> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.violation_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.timestampTextView.setText(items.get(position).getTimestamp().toString());

        holder.moreButton.setOnClickListener((View view) -> {
            Log.d(TAG, "Violation item button: click.");

            Context context = view.getContext();
            Intent intent = new Intent(context, ViolationDetailsActivity.class);
            intent.putExtra("VIOLATION", items.get(position).getId());
            context.startActivity(intent);

            System.out.println("*****************************************************");
            System.out.println(items.get(position));
            System.out.println(items.get(position).getId());

            Toast.makeText(context, items.get(position).getTimestamp().toString(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        TextView timestampTextView;
        Button moreButton;

        public ViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.violation_list_parent);
            timestampTextView = itemView.findViewById(R.id.violation_timestamp);
            moreButton = itemView.findViewById(R.id.violation_details);
        }
    }


}
