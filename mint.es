val proposedTokenHasSameIdAsFirstTxInput = OUTPUTS(0).tokens(0)._1 == SELF.id
val proposedTokenIsNonFungible = OUTPUTS(0).tokens(0)._2 == 1
val proposedTokenIsValidNFT = proposedTokenHasSameIdAsFirstTxInput && proposedTokenIsNonFungible

val expectedTokenName = INPUTS(0).R4[Coll[Byte]].get
val proposedTokenName = OUTPUTS(0).R4[Coll[Byte]].get
val tokenNameIsCorrect = expectedTokenName == proposedTokenName

val expectedPaymentAmount = INPUTS(0).R5[Long].get
val sentPaymentAmount = SELF.value
val paymentAmountIsCorrect = expectedPaymentAmount == sentPaymentAmount

val expectedReceiverAddress = INPUTS(0).R6[Coll[Byte]].get
val proposedReceiverAddress = OUTPUTS(0).propositionBytes
val receiverAddressIsCorrect = expectedReceiverAddress == proposedReceiverAddress

val tokenCanBeMinted = proposedTokenIsValidNFT &&
                       tokenNameIsCorrect &&
                       paymentAmountIsCorrect &&
                       receiverAddressIsCorrect &&
                       ergoNamesPk

sigmaProp(tokenCanBeMinted)