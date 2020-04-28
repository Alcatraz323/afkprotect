package io.alcatraz.facesaver.activities

import android.os.Bundle
import io.alcatraz.facesaver.R
import io.alcatraz.facesaver.extended.CompatWithPipeActivity

class ProfileGeneralActivity: CompatWithPipeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_general)
    }


}