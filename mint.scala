object ErgoNamesMintingContract {

    val script: String = s"""
      {
        // 2% per EIP24 standard format; could potentially be a data input
        val royaltyPercentage = 20
        val txFee = 1000000

        // Verify all the requirements for minting the NFT are met
        val mintToken = {
          // Verify token is an NFT
          val proposedTokenHasSameIdAsFirstTxInput = OUTPUTS(0).tokens(0)._1 == SELF.id
          val proposedTokenIsNonFungible = OUTPUTS(0).tokens(0)._2 == 1
          val proposedTokenSpecsOk = proposedTokenHasSameIdAsFirstTxInput && proposedTokenIsNonFungible

          // Verify the royalty percentage is correct
          val specifiedRoyalty = SELF.R4[Int].get
          val expectedRoyalty = 20
          val royaltyOk = specifiedRoyalty == expectedRoyalty

          // Verify name of token being issued is what was requested
          val expectedTokenName = SELF.R5[Coll[Byte]].get
          val proposedTokenName = OUTPUTS(0).R4[Coll[Byte]].get
          val tokenNameOk = expectedTokenName == proposedTokenName

          // Verify expected payment amount was received
          val expectedPaymentAmount = SELF.R6[Long].get
          val sentPaymentAmount = SELF.value
          val receivedPaymentAmountOk = expectedPaymentAmount == sentPaymentAmount

          // Verify that NFT is being sent to the sender of the payment
          val expectedReceiverAddress = SELF.R7[Coll[Byte]].get
          val proposedReceiverAddress = OUTPUTS(0).propositionBytes
          val receiverAddressOk = expectedReceiverAddress == proposedReceiverAddress

          // Verify correct payment amount is being collected
          val amountToCollect = sentPaymentAmount - 1000000
          val amountBeingCollected = OUTPUTS(1).value
          val collectedAmountOk = amountToCollect == amountBeingCollected

          // Verify payment is being sent to the right address
          val collectedByErgoNames = OUTPUTS(1).propositionBytes == ergoNamesPk.propBytes
          val paymentDetailsOk = collectedAmountOk && collectedByErgoNames

          proposedTokenSpecsOk &&
          royaltyOk &&
          tokenNameOk &&
          receivedPaymentAmountOk &&
          receiverAddressOk &&
          paymentDetailsOk
        }

        // In case of a refund, check that funds are going back to the sender
        val issueRefund = {
            val senderAddress = SELF.R7[Coll[Byte]].get
            val fundsAreGoingBackToSender = senderAddress == OUTPUTS(0).propositionBytes

            val amountToReturn = SELF.value - 1000000
            val correctAmountBeingReturned = amountToReturn == OUTPUTS(0).value

            fundsAreGoingBackToSender && correctAmountBeingReturned
        }


        val collectRoyalty = {
          // checking for empty registers is a way to prevent ErgoNames from just collecting funds from a valid minting request box without actually issuing an NFT.
          val isNotMintingRequest = !(SELF.R4[Int].isDefined) && !(SELF.R5[Coll[Byte]].isDefined) && !(SELF.R6[Long].isDefined) && !(SELF.R7[Coll[Byte]].isDefined)
          val beingCollectedByErgoNames = OUTPUTS(0).propositionBytes == ergoNamesPk.propBytes // could actually make royalties go to royalties contract

          isNotMintingRequest && beingCollectedByErgoNames
        }

        sigmaProp((mintToken || issueRefund || collectRoyalty) && ergoNamesPk)
      }
      """.stripMargin

}