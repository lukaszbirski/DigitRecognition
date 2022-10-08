package pl.birski.digitrecognition

class Result(probs: FloatArray, timeCost: Long) {

    var mNumber = 0
    var mProbability = 0f
    var mTimeCost: Long = 0

    init {
        mNumber = argmax(probs)
        mProbability = probs[mNumber]
        mTimeCost = timeCost
    }

    private fun argmax(probs: FloatArray): Int {
        var maxIdx = -1
        var maxProb = 0.0f
        for (i in probs.indices) {
            if (probs[i] > maxProb) {
                maxProb = probs[i]
                maxIdx = i
            }
        }
        return maxIdx
    }
}
