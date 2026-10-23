package com.example.smartpantry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.logic.Units;
import com.example.smartpantry.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom adapter that shows the pantry items from the database in a RecyclerView.
 * Each row has a round letter avatar and a coloured freshness strip on the left:
 * green = fresh, amber = expiring soon, red = expired, teal = no expiry date.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    public interface Listener {
        void onItemClick(PantryItem item);
        void onDeleteClick(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final Listener listener;
    private boolean highlightExpiring = true;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<PantryItem> newItems, boolean highlightExpiring) {
        items.clear();
        items.addAll(newItems);
        this.highlightExpiring = highlightExpiring;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PantryItem item = items.get(position);
        Context context = holder.itemView.getContext();

        holder.textName.setText(item.getName());
        holder.textQuantity.setText(Units.describe(item.getQuantity(), item.getUnit()));
        holder.textInitial.setText(Initials.of(item.getName()));

        int normalColor = ContextCompat.getColor(context, R.color.pantry_text_secondary);
        int warnColor = ContextCompat.getColor(context, R.color.pantry_warning);
        int soonColor = ContextCompat.getColor(context, R.color.pantry_soon);
        int okColor = ContextCompat.getColor(context, R.color.pantry_ok);
        int neutralColor = ContextCompat.getColor(context, R.color.pantry_primary);

        if (item.getExpiryDate().isEmpty()) {
            holder.textExpiry.setText(R.string.no_expiry);
            holder.textExpiry.setTextColor(normalColor);
            holder.statusStrip.setBackgroundColor(neutralColor);
        } else if (highlightExpiring && item.isExpiringSoon(AppPrefs.EXPIRY_WINDOW_DAYS)) {
            int days = item.daysUntilExpiry();
            if (days < 0) {
                holder.textExpiry.setText(context.getString(R.string.expired_on, item.getExpiryDate()));
                holder.textExpiry.setTextColor(warnColor);
                holder.statusStrip.setBackgroundColor(warnColor);
            } else {
                holder.textExpiry.setText(context.getString(R.string.expires_soon, item.getExpiryDate()));
                holder.textExpiry.setTextColor(warnColor);
                holder.statusStrip.setBackgroundColor(soonColor);
            }
        } else {
            holder.textExpiry.setText(context.getString(R.string.expires_on, item.getExpiryDate()));
            holder.textExpiry.setTextColor(normalColor);
            holder.statusStrip.setBackgroundColor(okColor);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.buttonDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final View statusStrip;
        final TextView textInitial;
        final TextView textName;
        final TextView textQuantity;
        final TextView textExpiry;
        final ImageButton buttonDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            statusStrip = itemView.findViewById(R.id.statusStrip);
            textInitial = itemView.findViewById(R.id.textInitial);
            textName = itemView.findViewById(R.id.textName);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            textExpiry = itemView.findViewById(R.id.textExpiry);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
