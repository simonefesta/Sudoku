package it.bff.sudoku.mainMenu.fragments;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.bff.sudoku.R;
import it.bff.sudoku.database.AppSudokuDatabase;
import it.bff.sudoku.database.SudokuScore;


class Adapter extends RecyclerView.Adapter<Adapter.Holder> implements View.OnLongClickListener
{

    private AppSudokuDatabase db;

    private List<SudokuScore> listSudokuScore;

    Adapter(List<SudokuScore> listSudokuScore, AppSudokuDatabase db)
    {
        this.db = db;
        this.listSudokuScore = listSudokuScore;
    }

    @NonNull
    @Override
    public Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        ConstraintLayout cl;
        cl = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_element, parent, false);
        cl.setLongClickable(true);
        cl.setOnLongClickListener(this);

        return new Adapter.Holder(cl);
    }

    @Override
    public void onBindViewHolder(@NonNull final Adapter.Holder holder, int position)
    {
        holder.tvName.setText(listSudokuScore.get(position).getPlayerName());
        holder.tvScore.setText(listSudokuScore.get(position).getPoints());
        holder.tvTime.setText(listSudokuScore.get(position).getTimer());
    }

    @Override
    public int getItemCount()
    {
        return listSudokuScore.size();
    }

    @Override
    public boolean onLongClick(View v)
    {
        int position = ((RecyclerView) v.getParent()).getChildAdapterPosition(v);
        remove(position);

        return true;
    }

    private void remove(int position){

        // remove from db
        db.sudokuDAO().delete(listSudokuScore.get(position));

        // remove from recycler view
        listSudokuScore.remove(position);

        // refresh recycler view
        notifyItemRemoved(position);
    }


    class Holder extends RecyclerView.ViewHolder
    {
        final TextView tvName;
        final TextView tvScore;
        final TextView tvTime;
        Holder(@NonNull View itemView)
        {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}