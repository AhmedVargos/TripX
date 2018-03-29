package fallenleafapps.com.tripplanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dd.morphingbutton.MorphingButton;

import java.util.Date;
import java.util.List;

import fallenleafapps.com.tripplanner.R;
import fallenleafapps.com.tripplanner.models.TripModel;
import fallenleafapps.com.tripplanner.ui.listeners.TripCardListener;

/**
 * Created by Vargos on 20-Mar-18.
 */

public class TripRecyclerAdapter extends RecyclerView.Adapter<TripRecyclerAdapter.TripViewHolder>{
    Context context;
    List<TripModel> tripsList;
    TripCardListener tripCardListener;
    int type;

    public  TripRecyclerAdapter(Context context, List<TripModel> tripsList, TripCardListener tripCardListener, int type){
        this.context = context;
        this.tripsList = tripsList;
        this.tripCardListener = tripCardListener;
        this.type = type;
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trip_card, parent,false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TripViewHolder holder, int position) {
        final TripModel tripModel = tripsList.get(position);
        if (type != 0){
            holder.startTrip.setVisibility(View.GONE);
        }
        holder.tripTitle.setText(tripModel.getTripName());
        holder.tripDate.setText(new Date(tripModel.getTripDate()).toString());
        Glide.with(context)
                .load(R.drawable.tripimage)
                .centerCrop()
                .into(holder.tripImage);

        holder.startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    tripCardListener.startTrip(tripModel,holder.startTrip);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.deleteTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tripCardListener.deleteTrip(tripModel);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tripCardListener.openTripDetails(tripModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripsList.size();
    }

    public class TripViewHolder extends RecyclerView.ViewHolder {
        ImageView tripImage;
        ImageButton deleteTrip;
        TextView tripTitle;
        TextView tripDate;
        MorphingButton startTrip;
        public TripViewHolder(View itemView) {
            super(itemView);

            tripImage = itemView.findViewById(R.id.item_trip_image_background);
            tripTitle = itemView.findViewById(R.id.item_trip_title);
            tripDate = itemView.findViewById(R.id.item_trip_date);
            startTrip = itemView.findViewById(R.id.item_trip_start);
            deleteTrip = itemView.findViewById(R.id.item_trip_delete);
        }
    }
}
