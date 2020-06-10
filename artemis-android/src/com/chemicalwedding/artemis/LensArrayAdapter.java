package com.chemicalwedding.artemis;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chemicalwedding.artemis.database.Lens;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class LensArrayAdapter extends BaseAdapter {
    private Context mContext;
    private List<Lens> lenses = new ArrayList<>();
    private ListView listView;

    public LensArrayAdapter(@NonNull Context context, @LayoutRes ArrayList<Lens> list, ListView listView) {
        mContext = context;
        lenses = list;
        this.listView = listView;
    }

    @NonNull
    @Override
    public View getView (int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.lens_list_item, parent, false);
        }

<<<<<<< HEAD
            Lens lens = lenses.get(position);

            TextView millis = listItem.findViewById(R.id.millis);
            NumberFormat nf = NumberFormat.getInstance();
            String millisText = nf.format(lens.getFL()) + " mm";
            millis.setText(millisText);

            AngleView angle = listItem.findViewById(R.id.angle);
            angle.angle = ArtemisMath.getInstance().calculateViewingAngle(lens.getFL())[0];
            angle.invalidate();

            TextView angleText = listItem.findViewById(R.id.angleText);
            NumberFormat format = new DecimalFormat("###.#");
            angleText.setText(format.format(angle.angle) + "º");

            final CheckBox checkBox = listItem.findViewById(R.id.checkbox);
            final RelativeLayout checkableLayout = (RelativeLayout) listItem;

//        listItem.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
//                    boolean isChecked = checkableLayout.isChecked();
//                    checkBox.setChecked(isChecked);
//                    return false;
//                }
//                return false;
//            }
//        });


//        boolean isChecked = ((CheckableRelativeLayout) listItem).isChecked();
//        checkBox.setChecked(isChecked);
//        checkBox.invalidate();
        boolean isChecked = listView.isItemChecked(position);
        checkBox.setChecked(isChecked);

         return listItem;
=======
        Lens lens = lenses.get(position);

        TextView millis = listItem.findViewById(R.id.millis);
        NumberFormat nf = NumberFormat.getInstance();
        String millisText = nf.format(lens.getFL()) + " mm";
        millis.setText(millisText);

        AngleView angle = listItem.findViewById(R.id.angle);
        angle.angle = ArtemisMath.getInstance().calculateViewingAngle(lens.getFL())[0];
        angle.invalidate();

        TextView angleText = listItem.findViewById(R.id.angleText);
        NumberFormat format = new DecimalFormat("###.#");
        angleText.setText(format.format(angle.angle) + "º");

        final CheckBox checkBox = listItem.findViewById(R.id.checkbox);
        boolean isChecked = listView.isItemChecked(position);
        checkBox.setChecked(isChecked);

        return listItem;
>>>>>>> ed0b9bd (Look and feel changes)
    }

    public int getCount(){
        return this.lenses.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Lens getItem(int position){
        return this.lenses.get(position);
    }
}
