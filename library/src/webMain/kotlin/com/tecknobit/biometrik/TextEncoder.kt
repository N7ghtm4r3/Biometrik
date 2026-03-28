package com.tecknobit.biometrik

import org.khronos.webgl.Uint8Array

/**
 * The `TextEncoder` class maps the native [TextEncoder](https://developer.mozilla.org/en-US/docs/Web/API/TextEncoder)
 * API
 *
 * @author Tecknobit - N7ghtm4r3
 */
internal external class TextEncoder {

    /**
     * Method used to encode an input into a [Uint8Array].
     *
     * Official documentation [here](https://developer.mozilla.org/en-US/docs/Web/API/TextEncoder/encode)
     *
     * @return the encoded input as [Uint8Array]
     */
    fun encode(
        input: String,
    ): Uint8Array

}