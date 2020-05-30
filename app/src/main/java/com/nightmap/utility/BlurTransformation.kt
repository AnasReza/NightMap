package com.nightmap.utility

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.squareup.picasso.Transformation


class BlurTransformation: Transformation {
    private var rs:RenderScript?=null
    constructor(context: Context){
        rs= RenderScript.create(context)
    }
    override fun key(): String {
        return "blur"
    }

    override fun transform(bitmap: Bitmap?): Bitmap {
        var blurredBitmap:Bitmap=bitmap!!.copy(Bitmap.Config.ARGB_8888,true)

        var input:Allocation=Allocation.createFromBitmap(rs,blurredBitmap,Allocation.MipmapControl.MIPMAP_FULL,Allocation.USAGE_SHARED)
        var output:Allocation=Allocation.createTyped(rs,input.type)

        var script:ScriptIntrinsicBlur= ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        script.setInput(input)

        script.setRadius(10F)
        script.forEach(output)
        output.copyTo(blurredBitmap)
        bitmap.recycle()

        return blurredBitmap
    }
}