package com.example.orientationrace.gardens;

import static com.example.orientationrace.activities.RaceActivity.MQTTCONNECTION;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orientationrace.MqttManager;
import com.example.orientationrace.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;

public class GardensAdapter extends RecyclerView.Adapter<GardensViewHolder> implements MqttCallback {

    private static final String TAG = "TAGListOfGardens, GardensAdapter";

    private final GardensDataset dataset; // reference to the dataset

    private final Context context;
    public boolean[] itemClickedState;  // Keeps track of clicked state for each item
    private int checkpointsReached;
    private static final String TOPIC_CHECKPOINTS = "madridOrientationRace/checkpoints";
    private MqttManager mqttManager;

    public GardensAdapter(GardensDataset dataset, Context context) {
        super();
        Log.d(TAG, "GardensAdapter() called");
        this.dataset = dataset;
        this.context = context;
        this.itemClickedState = new boolean[6];
        Arrays.fill(itemClickedState, false);
        mqttManager = MqttManager.getInstance();
        checkpointsReached = 0;
    }

    // ------ Implementation of methods of RecyclerView.Adapter ------ //

    @NonNull
    @Override
    public GardensViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // this method has to actually inflate the item view and return the view holder.
        // it does not give values to the elements of the view holder.
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.garden, parent, false);
        return new GardensViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GardensViewHolder holder, int position) {
        // this method gives values to the elements of the view holder 'holder'
        // (values corresponding to the item in 'position')

        final Garden garden = dataset.getGardenAtPosition(position);
        Long gardenKey = garden.getKey();
        int itemPosition = position;

        Log.d(TAG, "Garden onBindViewHolder() called for element in position " + position);
        holder.bindValues(garden);

        // Check the clicked state of the item
        if (itemClickedState[position]) {
            // Item is clicked, set a visual indication (e.g., gray background)
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
            holder.itemView.setClickable(false);  // Disable click events
            holder.gardenName.setText("Reached");
        } else {
            // Item is not clicked, set the default state
            holder.itemView.setClickable(true);  // Enable click events
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!itemClickedState[itemPosition]) {
                        showPopup(v, itemPosition);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataset.getSize();
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {

    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    public interface OnLongClickListener {
        void onLongClick(int position, Garden garden);
    }

    // ------ Other methods useful for the app ------ //

    public Long getKeyAtPosition(int pos) {
        return (dataset.getKeyAtPosition(pos));
    }

    public int getPositionOfKey(Long searchedkey) {
        //Log.d(TAG, "getPositionOfKey() called for key " + searchedkey + ", returns " + position);
        return dataset.getPositionOfKey(searchedkey);
    }

    // Method to show a Popup window requesting the user to confirm that he reached the checkpoint
    public void showPopup(View view, int gardenPosition) {
        // Create a Dialog object
        Dialog popupDialog = new Dialog(context);

        // Set the content view to the layout created for the popup
        popupDialog.setContentView(R.layout.garden_reached_confirmation_popup);

        // Get reference to the "Cancel" button in the popup layout and add onClick Listener
        Button bCancel = popupDialog.findViewById(R.id.buttonCancel);
        bCancel.setOnClickListener(v -> {
            // Close popup when the button is clicked
            popupDialog.dismiss();
        });

        // Get reference to the "Confirm" button in the popup layout and add onClick Listener
        Button bConfirm = popupDialog.findViewById(R.id.buttonConfirm);
        bConfirm.setOnClickListener(v -> {
            // Update the clicked state for the item;
            itemClickedState[gardenPosition] = true;
            // Notify the adapter that the data set has changed
            notifyItemChanged(gardenPosition);
            checkpointsReached++;

            if (checkpointsReached == 6) {
                publishWinner();
                Log.d(MQTTCONNECTION, "Publish winner");
            } else {
                publishCheckpointReached(checkpointsReached);
                Log.d(MQTTCONNECTION, "Checkpoint Reached");
            }

            // Close popup when the button is clicked
            popupDialog.dismiss();
        });

        // Show the popup
        popupDialog.show();
    }

    private void publishWinner() {
        String message = mqttManager.getClientId() + " reached all Checkpoints and won!";
        try {
            mqttManager.publishMessage(TOPIC_CHECKPOINTS, message);
        } catch (MqttException e) {
            Log.d(MQTTCONNECTION, "No Publishing");
        }
    }

    private void publishCheckpointReached(int checkpointNumber) {
        String message = mqttManager.getClientId() + " reached Checkpoint Number: " + checkpointNumber;
        try {
            mqttManager.publishMessage(TOPIC_CHECKPOINTS, message);
            Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        } catch (MqttException e) {
            Log.d(MQTTCONNECTION, "No Publishing");
        }
    }
}
