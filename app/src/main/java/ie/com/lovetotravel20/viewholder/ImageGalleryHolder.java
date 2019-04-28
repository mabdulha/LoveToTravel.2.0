package ie.com.lovetotravel20.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.squareup.picasso.Picasso;

import ie.com.lovetotravel20.R;
import android.widget.ImageView;

public class ImageGalleryHolder extends RecyclerView.ViewHolder {

    public ImageGalleryHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setImage(String imageUrl){

        ImageView post_image = (ImageView) itemView.findViewById(R.id.image_card_display);
        Picasso.get()
                .load(imageUrl)
                .resize(480,250)
                .centerInside()
                .into(post_image);
    }
}