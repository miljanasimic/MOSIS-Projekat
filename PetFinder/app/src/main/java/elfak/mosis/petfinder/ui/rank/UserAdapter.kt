package elfak.mosis.petfinder.ui.rank

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import elfak.mosis.petfinder.R
import elfak.mosis.petfinder.data.model.UserRank
import kotlinx.coroutines.NonDisposableHandle.parent

class UserAdapter(private val context: Context, private val dataSource: List<UserRank>): BaseAdapter(){
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(p: Int): UserRank {
        return dataSource[p]
    }

    override fun getItemId(p: Int): Long {
        return p.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.list_item_user_rank, parent, false)
        val userImage = rowView.findViewById<ImageView>(R.id.user_photo)
        val userFullName = rowView.findViewById<TextView>(R.id.user_full_name)
        val userEmail = rowView.findViewById<TextView>(R.id.user_email)
        val userRank = rowView.findViewById<TextView>(R.id.user_rank)
        val user = getItem(position)
        userFullName.text = user.firstName.plus(" ").plus(user.lastName)
        userEmail.text= user.email
        userRank.text = user.rank.toString()
        if(user.imageBitmap!=null){
            userImage.setImageBitmap(user.imageBitmap)
            userImage.setColorFilter(Color.parseColor("#80000000"))
            userImage.rotation= (90).toFloat()
        }
        val currentUser = Firebase.auth.currentUser
        if(currentUser!=null && currentUser.email==user.email)
            rowView.findViewById<LinearLayout>(R.id.item_user_rank).background=ContextCompat.getDrawable(context,R.drawable.rounded_rank_list_item_selected)
        return rowView
    }
}