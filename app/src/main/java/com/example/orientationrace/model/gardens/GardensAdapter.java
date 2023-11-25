package com.example.orientationrace.model.gardens;

import static com.example.orientationrace.views.activities.RaceActivity.MQTTCONNECTION;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orientationrace.model.MqttManager;
import com.example.orientationrace.R;
import com.example.orientationrace.views.activities.GardenLocationActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;

/**
 * Adapter class for managing the dataset of gardens in a RecyclerView.
 * Extends RecyclerView.Adapter and implements MqttCallback.
 */
public class GardensAdapter extends RecyclerView.Adapter<GardensViewHolder> implements MqttCallback {

    // Tag used for logging
    private static final String TAG = "TAGListOfGardens, GardensAdapter";

    // Reference to the dataset
    private final GardensDataset dataset;

    private final Context context;

    // Keeps track of clicked state for each item
    public boolean[] itemClickedState;

    //Number of checkpoints reached by the user.
    private int checkpointsReached;

    // Topic for MQTT communication related to checkpoints.
    private static final String TOPIC_CHECKPOINTS = "madridOrientationRace/checkpoints";

    // Manager for handling MQTT communication.
    private final MqttManager mqttManager;

    /**
     * Constructs a GardensAdapter with the specified dataset and context.
     *
     * @param dataset The dataset of gardens.
     * @param context The application context.
     */
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

    /**
     * Called when RecyclerView needs a new ViewHolder.
     *
     * @param parent   The ViewGroup into which the new View will be added.
     * @param viewType The type of the new View.
     * @return A new GardensViewHolder that holds a View with the given layout.
     */
    @NonNull
    @Override
    public GardensViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // This method has to actually inflate the item view and return the view holder.
        // It does not give values to the elements of the view holder.
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.garden, parent, false);
        return new GardensViewHolder(v);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method gives values
     * to the elements of the view holder 'holder'
     *
     * @param holder   The ViewHolder that should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's dataset.
     */
    @Override
    public void onBindViewHolder(GardensViewHolder holder, int position) {
        final Garden garden = dataset.getGardenAtPosition(position);
        Long gardenKey = garden.getKey();
        int itemPosition = position;

        Log.d(TAG, "Garden onBindViewHolder() called for element in position " + position);
        holder.bindValues(garden);

        // Check the clicked state of the item
        if (itemClickedState[position]) {
            // Item is clicked, set a visual indication (gray background and change text)
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
            holder.gardenName.setText("Reached");
            holder.itemView.setClickable(false);  // Disable click events
        } else {
            // Item is not clicked, set the default state
            holder.itemView.setClickable(true);  // Enable click events
            holder.itemView.setOnLongClickListener(v -> {
                if (!itemClickedState[itemPosition]) {
                    showPopup(itemPosition);
                }
                return true;
            });

            holder.itemView.setOnClickListener(view -> {
                if (!itemClickedState[itemPosition]) {
                    Intent intent = new Intent(context, GardenLocationActivity.class);

                    intent.putExtra("gardenLat", garden.getLatitude());
                    intent.putExtra("gardenLong", garden.getLongitude());

                    // Start the new activity
                    context.startActivity(intent);
                }
            });
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in the data set.
     */
    @Override
    public int getItemCount() {
        return dataset.getSize();
    }

    public void setOnLongClickListener() {

    }

    public void setOnItemClickListener() {

    }


    // ------ Implementation of methods of MqttCallback ------ //

    /**
     * Called when the connection to the server is lost.
     *
     * @param cause The reason for the connection loss.
     */
    @Override
    public void connectionLost(Throwable cause) {

    }

    /**
     * Called when a message arrives from the server.
     *
     * @param topic   The topic on which the message was received.
     * @param message The received message.
     * @throws Exception Any exception that occurs while processing the message.
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }

    /**
     * Called when delivery for a message has been completed, and all acknowledgments have been received.
     *
     * @param token The delivery token associated with the message.
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }


    // ------ Other methods useful for the app ------ //

    /**
     * Gets the key of the garden at the specified position.
     *
     * @param pos The position of the garden in the adapter.
     * @return The key of the garden.
     */
    public Long getKeyAtPosition(int pos) {
        return (dataset.getKeyAtPosition(pos));
    }

    /**
     * Gets the position of the garden with the specified key in the adapter.
     *
     * @param searchedkey The key of the garden.
     * @return The position of the garden in the adapter.
     */
    public int getPositionOfKey(Long searchedkey) {
        //Log.d(TAG, "getPositionOfKey() called for key " + searchedkey + ", returns " + position);
        return dataset.getPositionOfKey(searchedkey);
    }

    /**
     * Method to show a Popup window requesting confirmation that the user reached the checkpoint.
     *
     * @param gardenPosition The position of the garden item in the adapter.
     */
    public void showPopup(int gardenPosition) {
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

    /**
     * Publishes a message indicating that the user reached all checkpoints and won.
     */
    private void publishWinner() {
        String message = mqttManager.getClientId() + " reached all Checkpoints and won!";
        try {
            mqttManager.publishMessage(TOPIC_CHECKPOINTS, message);
        } catch (MqttException e) {
            Log.d(MQTTCONNECTION, "No Publishing");
        }

        // Create a Dialog object
        Dialog popupDialog = new Dialog(context);

        // Set the content view to the layout created for the popup
        popupDialog.setContentView(R.layout.race_won_popup);

        // Show the popup
        popupDialog.show();

    }

    /**
     * Publishes a message indicating that the user reached a specific checkpoint.
     *
     * @param checkpointNumber The number of the reached checkpoint.
     */
    private void publishCheckpointReached(int checkpointNumber) {
        String message = mqttManager.getClientId() + " reached Checkpoint Number: " + checkpointNumber;
        try {
            mqttManager.publishMessage(TOPIC_CHECKPOINTS, message);
        } catch (MqttException e) {
            Log.d(MQTTCONNECTION, "No Publishing");
        }
    }
}
