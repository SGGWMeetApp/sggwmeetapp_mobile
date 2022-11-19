package pl.sggw.sggwmeet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.findNavController

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.mainToolbar))

        val button = findViewById<Button>(R.id.popupButton)

        button.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this,button)
            popupMenu.menuInflater.inflate(R.menu.select_view_menu,popupMenu.menu)
            popupMenu.show()

            val navController = findNavController(R.id.nav_host_fragment)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.menu_start -> {
                        navController.navigateUp()
                        navController.navigate(R.id.startFragment)
                    }
                    R.id.menu_next -> {
                        navController.navigateUp()
                        navController.navigate(R.id.nextFragment)
                    }
                    R.id.menu_login -> {
                        navController.navigateUp()
                        navController.navigate(R.id.loginFragment)
                    }
                    R.id.menu_scrolling -> {
                        navController.navigateUp()
                        navController.navigate(R.id.scrollingFragment)
                    }
                    R.id.menu_register -> {
                        navController.navigateUp()
                        navController.navigate(R.id.registerFragmemt)
                    }
                }
                true
            })
        }
    }
}