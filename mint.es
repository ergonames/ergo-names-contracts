val buyerOutput = {
    val tokenToIssue = OUTPUTS(0).getOrElse(0, (INPUTS(0).id, 0L))
    INPUTS(0).id == tokenToIssue._1 && tokenToIssue._2 == 1L && OUTPUTS(0).value == $ergAmountL && OUTPUST(0).propositionBytes == fromBase64("$userAddress")
}

val returnFunds = {
    val total = INPUTS(0L, {(x:Long, b:Box) => x + b.value})
    OUTPUTS(0).value >= total && OUTPUTS(0).propositionBytes] == ("$userAddress")
}

sigmaProp(buyerOutput || returnFunds)