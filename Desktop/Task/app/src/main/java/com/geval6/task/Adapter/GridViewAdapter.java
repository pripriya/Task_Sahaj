package com.geval6.task.Adapter;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.geval6.task.Model.ModelClass;
import com.geval6.task.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;


public class GridViewAdapter extends ArrayAdapter{

    private Context context;
    private int layoutResourceId;
    private ArrayList<ModelClass> mGridData = new ArrayList<ModelClass>();
    boolean isBackVisible = false;

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<ModelClass> mGridData) {
        super(context, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.mGridData = mGridData;
    }

    public void setGridData(ArrayList<ModelClass> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.grid_item, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) row.findViewById(R.id.grid_item_title);
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
            row.setTag(holder);
            final AnimatorSet setRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.right_flip);
            final AnimatorSet setLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.left_flip);
            final ModelClass item = mGridData.get(position);

            holder.titleTextView.setText(Html.fromHtml(item.getTitle()));
            holder.titleTextView.setVisibility(View.GONE);
            Picasso.with(context).load(item.getImage()).into(holder.imageView);

    row.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if(!isBackVisible){
            setRightOut.setTarget(holder.imageView);
            setLeftIn.setTarget(holder.titleTextView);
            setRightOut.start();
            setLeftIn.start();
            isBackVisible = true;
        }
        else{
            holder.titleTextView.setVisibility(View.VISIBLE);
            setRightOut.setTarget(holder.titleTextView);
            setLeftIn.setTarget(holder.imageView);
            setRightOut.start();
            setLeftIn.start();
            isBackVisible = false;
          }
        }
    });
        } else {
            holder = (ViewHolder) row.getTag();
        }
        return row;
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }
}

