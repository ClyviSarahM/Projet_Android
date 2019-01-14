package fr.uga.miashs.chooseevent.adapters;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import fr.uga.miashs.chooseevent.R;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private int selected_position = -1;

    private SelectionTracker selectionTracker;

    public void setSelectionTracker(SelectionTracker tracker) {
        this.selectionTracker = tracker;
    }

    final class  EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        long id;
        int position;
        private final TextView title;
        private final TextView begin;
        private final TextView end;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.event_title);
            begin = itemView.findViewById(R.id.event_begin);
            end = itemView.findViewById(R.id.event_end);
        }

        @Override
        public void onClick(View view) {
            if (getAdapterPosition() == RecyclerView.NO_POSITION) return;
            notifyItemChanged(selected_position);
            selected_position = getAdapterPosition();
            notifyItemChanged(selected_position);
        }

        public ItemDetailsLookup.ItemDetails getDetails() {
            Log.d("getDetails","details...");
            return new ItemDetailsLookup.ItemDetails() {

                @Override
                public int getPosition() {
                    return position;
                }

                @Nullable
                @Override
                public Object getSelectionKey() {
                    return id;
                }
            };
        }
    }


    public static final class EventDetailsLookup extends ItemDetailsLookup{
        private final RecyclerView mrecyclerView;

        public EventDetailsLookup(RecyclerView recyclerView){
            mrecyclerView = recyclerView;
        }

        public ItemDetails getItemDetails(MotionEvent event){
            View view = mrecyclerView.findChildViewUnder(event.getX(), event.getY());

            if(view != null){
                RecyclerView.ViewHolder holder = mrecyclerView.getChildViewHolder(view);
                if(holder instanceof EventViewHolder){
                    return ((EventViewHolder) holder).getDetails();
                }
            }
            return null;
        }
    }


    private final LayoutInflater mInflater;
    private Cursor data;

    private ContentResolver cr;
    private Context mContext;

    public EventAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        cr = context.getContentResolver();
        mContext = context;
    }

    @NonNull
    @Override
    public EventAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)  {
        View itemView = mInflater.inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.EventViewHolder holder, int position) {
        if (data == null || data.getCount() == 0) {
            holder.title.setText("No events");
            holder.begin.setText("");
            holder.end.setText("");
        } else {
            // this one is relative
            //data.move(position);
            // this one is absolute
            data.moveToPosition(position);
            holder.title.setText(data.getString(data.getColumnIndex(CalendarContract.Events.TITLE)));

            long startT = data.getLong(data.getColumnIndex(CalendarContract.Events.DTSTART));
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(startT);
            DateFormat df = DateFormat.getDateInstance();

            holder.begin.setText(df.format(c.getTime()));

            long endT =data.getLong(data.getColumnIndex(CalendarContract.Events.DTEND));
            c.setTimeInMillis(endT);

            holder.end.setText(df.format(c.getTime()));

            holder.id=getItemId(position);
            holder.position=position;


            holder.itemView.setActivated(selectionTracker.isSelected(getItemId(position)));
            holder.itemView.setBackgroundColor(selectionTracker.isSelected(getItemId(position)) ? Color.GREEN : Color.TRANSPARENT);
        }
    }

    private static String[] PROJECTION = new String[] {CalendarContract.Events._ID, CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND};



    public void readEvents(long time) {
        if (ContextCompat.checkSelfPermission(mContext,  Manifest.permission.READ_CALENDAR)  == PackageManager.PERMISSION_GRANTED) {

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(time);

            long begin = c.getTimeInMillis();

            c.add(Calendar.DATE,1);

            long end = c.getTimeInMillis();
            // first arg end
            // second args begin
            String select = CalendarContract.Events.DTSTART+"<"+end+" AND "+begin+"<"+CalendarContract.Events.DTEND;
            data = cr.query(CalendarContract.Events.CONTENT_URI, PROJECTION, select, null, null);
            selectionTracker.clearSelection();
            this.notifyDataSetChanged();
        }
    }

    public void readEvents(int year, int month, int dayOfMonth) {

        Calendar c = Calendar.getInstance();
        c.set(year,month,dayOfMonth);
        readEvents(c.getTimeInMillis());

    }


    @Override
    public long getItemId(int position) {
        data.moveToPosition(position);
        return data.getLong(data.getColumnIndex(CalendarContract.Events._ID));
    }

    @Override
    public int getItemCount() {
        if (data==null) return 1;
        return data.getCount();
    }
}
