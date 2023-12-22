package cat.tecnocampus.mobileapps.practicafinal.mariaperegrinausieto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    ArrayList<User> data;

    public UserAdapter(ArrayList<User> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        ViewHolder vh=new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = data.get(position);
        holder.userEmail.setText(user.getEmail());
        holder.numActivities.setText(String.valueOf(user.getActivities().size()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userEmail, numActivities;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmail=itemView.findViewById(R.id.tvUser);
            numActivities=itemView.findViewById(R.id.tvNum);
        }
    }
}
