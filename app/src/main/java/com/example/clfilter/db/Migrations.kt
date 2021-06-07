package com.example.clfilter.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("update smart_record set type = 'BS' where busType is null")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE INDEX IF NOT EXISTS index_smart_block_card_cardNo ON smart_block_card (cardNo)")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE smart_unionpaycode ( id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, version TEXT, type TEXT, organizationIndex TEXT, mobileAppFlag TEXT, mobileAppOrgCode TEXT, qrcodeEffectiveTime TEXT, industryScopeOfUse TEXT, batchNo TEXT, appPublicKeyForm TEXT, appPublicKey TEXT, organizationExpirationTime TEXT, organizationSign TEXT,qrcodeIdentifyingCode TEXT, qrcodeGeneratorTime TEXT, userFlag TEXT, industryCustomDataLen TEXT, industryCustomData TEXT, appSign TEXT, turnoverNo TEXT, validitySign TEXT, cardCategory TEXT, cardMainInfoBody TEXT, cardMainType TEXT, cardSubType TEXT, tradeTime INTEGER, tradeType TEXT, realTransactAmount INTEGER, castCoinAmount INTEGER, transactAmount INTEGER, originalAmount INTEGER, appTradeCount INTEGER, cardSerialNo TEXT, randomNumber TEXT, cardValidityPeriod TEXT, cipherTextInfoData TEXT, appCipherText TEXT, cardIssuerAppData TEXT, onlineInfoType TEXT, onlineModuleId TEXT, onlineMerchant TEXT, onlineBatchNo TEXT, offLineQrDataContent TEXT, codeData TEXT, responseCode TEXT, tradeMoneyOrBatchNo TEXT, localDate TEXT, localTime TEXT, expirationDate TEXT, settlementDate TEXT, acquiringInstitutionIdCode TEXT, RRN TEXT, authIdResponseBCD TEXT, addResponseData TEXT, cardMasterAccount TEXT, authIdResponseASC TEXT, originalOffLineQrRecordAddress TEXT, tradeWay TEXT)")
        database.execSQL("CREATE TABLE smart_unionpaycode_key ( id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, lastUpdateDate TEXT, certificateIndexNumber TEXT, certificateType TEXT, codePlatformIdentification TEXT, certificateInvalidDate TEXT, certificateSerialNumber TEXT, codePublicKeySignAlgorithmIdentification TEXT, codePublicKeyEncryptAlgorithmIdentification TEXT, codePublicKeyParamIdentification TEXT, codePublicKeyLength TEXT, codePublicKey TEXT)")
        database.execSQL("ALTER TABLE smart_device ADD COLUMN 'filialeName' TEXT")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE INDEX IF NOT EXISTS index_smart_white_card_cardNo ON smart_white_card (cardNo)")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_device ADD COLUMN 'whiteMark' TEXT")
        database.execSQL("ALTER TABLE smart_mission ADD COLUMN 'lineNo' TEXT")
        database.execSQL("ALTER TABLE smart_unionpay_message ADD COLUMN 'msgType' TEXT")
        database.execSQL("ALTER TABLE smart_unionpay_message ADD COLUMN 'consumeNo' TEXT")
    }
}


val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 白规则添加
        database.execSQL("CREATE TABLE smart_white_rule (id INTEGER PRIMARY KEY NOT NULL, whiteVersion INTEGER, cityCode TEXT, cardIssuerCode TEXT, inn TEXT, bin TEXT, numberType TEXT)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_smart_white_rule_cardIssuerCode_inn_bin ON smart_white_rule (cardIssuerCode, inn, bin)")
        // 黑名单修改
        database.execSQL("ALTER TABLE smart_block_card ADD COLUMN 'type' TEXT")
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 济南机场添加
        database.execSQL("CREATE TABLE smart_airport (id INTEGER PRIMARY KEY NOT NULL, uid TEXT, code TEXT, time TEXT, expiration_time TEXT, price INTEGER, type INTEGER, tradeTime INTEGER, state INTEGER)")
    }
}

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_rule ADD COLUMN 'transLimited' INTEGER")
    }
}

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_mission ADD COLUMN 'fileSize' INTEGER DEFAULT 0 NOT NULL")
    }
}

val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 支付宝-通卡连城
        database.execSQL("CREATE TABLE smart_alipay_tk (id INTEGER PRIMARY KEY NOT NULL, basePrice INTEGER, transactAmount INTEGER, userId TEXT, cardNo TEXT, cardData TEXT, transactTime INTEGER, record TEXT, codeIssuerNo TEXT, cardIssuerNo TEXT, discount INTEGER, posId TEXT, recordId TEXT, code TEXT)")
    }
}

val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_key ADD COLUMN 'decodeValue' TEXT")
    }
}

val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP INDEX IF EXISTS cardMainInfoBody")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_smart_card_cardSnr_recordType_tradeTime ON smart_card (cardSnr, recordType, tradeTime)")
    }
}

val MIGRATION_13_14 = object : Migration(13, 14) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_key ADD COLUMN 'createTime' INTEGER")
    }
}

val MIGRATION_14_15 = object : Migration(14, 15) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_key ADD COLUMN 'ucTermNr' TEXT")
        database.execSQL("ALTER TABLE smart_key ADD COLUMN 'ucMerchartNr' TEXT")
    }
}

val MIGRATION_15_16 = object : Migration(15, 16) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //OnlineBankCardEntity中删除了Tag8F字段，这里做迁移
        //1.创建新表
        database.execSQL("CREATE TABLE smart_online_bankcard_new (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `turnoverNo` TEXT, `validitySign` TEXT, `cardCategory` TEXT, `recordType` TEXT, `tradeWay` TEXT, `cardMainInfoBody` TEXT, `cardMainType` TEXT, `cardSubType` TEXT, `tradeTime` INTEGER, `tradeType` TEXT, `realTransactAmount` INTEGER, `castCoinAmount` INTEGER, `transactAmount` INTEGER, `originalAmount` INTEGER, `appTradeCount` TEXT, `pan` TEXT, `cardSerialNo` TEXT, `randomNumber` TEXT, `cardValidityPeriod` TEXT, `cipherTextInfoData` TEXT, `appCipherText` TEXT, `cardIssuerAppData` TEXT, `onlineInfoType` TEXT, `transferDn` TEXT, `multiTicketGetonOrgCode` TEXT, `multiTicketGetonLineCode` TEXT, `multiTicketGetonBusCode` TEXT, `multiTicketGetonDeviceCode` TEXT, `multiTicketGetonUpDn` TEXT, `multiTicketGetonStation` TEXT, `multiTicketGetonTime` INTEGER, `authorizeAmount` TEXT, `otherAmount` TEXT, `terminalValidateResult` TEXT, `phn` TEXT, `currency` TEXT, `tradeDate` TEXT, `aip` TEXT, `terminalCapability` TEXT, `dedicatedFilename` TEXT, `productIdentificationInfor` TEXT, `appVersion` TEXT, `authorizeResponseCode` TEXT, `interfaceDeviceSequence` TEXT, `cardIssuerAuthorizeCode` TEXT, `onlineModuleId` TEXT, `onlineMerchant` TEXT, `trackSecAppData` TEXT, `onlineBatchNo` TEXT, `preAuthorizedGetonRecordAlgorithm` TEXT, `responseCode` TEXT, `tradeMoneyOrBatchNo` TEXT, `localDate` TEXT, `localTime` TEXT, `expirationDate` TEXT, `settlementDate` TEXT, `acquiringInstitutionIdCode` TEXT, `RRN` TEXT, `authIdResponseBCD` TEXT, `addResponseData` TEXT, `cardMasterAccount` TEXT, `authIdResponseASC` TEXT)")
        //2.拷贝旧表数据
        database.execSQL("INSERT INTO smart_online_bankcard_new(id,turnoverNo,validitySign,cardCategory,recordType,tradeWay,cardMainInfoBody,cardMainType,cardSubType,tradeTime,tradeType,realTransactAmount,castCoinAmount,transactAmount,originalAmount,appTradeCount,pan,cardSerialNo,randomNumber,cardValidityPeriod,cipherTextInfoData,appCipherText,cardIssuerAppData,onlineInfoType,transferDn,multiTicketGetonOrgCode,multiTicketGetonLineCode,multiTicketGetonBusCode,multiTicketGetonDeviceCode,multiTicketGetonUpDn,multiTicketGetonStation,multiTicketGetonTime,authorizeAmount,otherAmount,terminalValidateResult,phn,currency,tradeDate,aip,terminalCapability,dedicatedFilename,productIdentificationInfor,appVersion,authorizeResponseCode,interfaceDeviceSequence,cardIssuerAuthorizeCode,onlineModuleId,onlineMerchant,trackSecAppData,onlineBatchNo,preAuthorizedGetonRecordAlgorithm,responseCode,tradeMoneyOrBatchNo,localDate,localTime,expirationDate,settlementDate,acquiringInstitutionIdCode,RRN,authIdResponseBCD,addResponseData,cardMasterAccount,authIdResponseASC) SELECT id,turnoverNo,validitySign,cardCategory,recordType,tradeWay,cardMainInfoBody,cardMainType,cardSubType,tradeTime,tradeType,realTransactAmount,castCoinAmount,transactAmount,originalAmount,appTradeCount,pan,cardSerialNo,randomNumber,cardValidityPeriod,cipherTextInfoData,appCipherText,cardIssuerAppData,onlineInfoType,transferDn,multiTicketGetonOrgCode,multiTicketGetonLineCode,multiTicketGetonBusCode,multiTicketGetonDeviceCode,multiTicketGetonUpDn,multiTicketGetonStation,multiTicketGetonTime,authorizeAmount,otherAmount,terminalValidateResult,phn,currency,tradeDate,aip,terminalCapability,dedicatedFilename,productIdentificationInfor,appVersion,authorizeResponseCode,interfaceDeviceSequence,cardIssuerAuthorizeCode,onlineModuleId,onlineMerchant,trackSecAppData,onlineBatchNo,preAuthorizedGetonRecordAlgorithm,responseCode,tradeMoneyOrBatchNo,localDate,localTime,expirationDate,settlementDate,acquiringInstitutionIdCode,RRN,authIdResponseBCD,addResponseData,cardMasterAccount,authIdResponseASC FROM smart_online_bankcard")
        //3.删除旧表
        database.execSQL("DROP TABLE smart_online_bankcard")
        //4.将新表重命名为旧表
        database.execSQL("ALTER TABLE smart_online_bankcard_new RENAME TO smart_online_bankcard")

        // 新颜
        database.execSQL("CREATE TABLE smart_xinyan (id INTEGER PRIMARY KEY NOT NULL, qrCode TEXT, qrCodeData TEXT, bizData TEXT, bizChannelCode TEXT, bizCustomPrice TEXT, bizTicket TEXT, bizCardIssuerNo TEXT, bizCardType TEXT, bizUserId TEXT, bizForwardFlag TEXT, bizTicketType TEXT, bizTicketPrice TEXT, bizTicketNo TEXT, bizTicketRFU TEXT, transactTime INTEGER, transactAmount INTEGER, basicPrice INTEGER, discount INTEGER)")
    }
}

val MIGRATION_16_17 = object : Migration(16, 17) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_device ADD COLUMN 'voiceVersion' INTEGER")
    }
}

val MIGRATION_17_18 = object : Migration(17, 18) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_device ADD COLUMN 'featureVersion' INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("CREATE TABLE smart_face (id INTEGER PRIMARY KEY NOT NULL, featureId TEXT, tradePrice INTEGER, version INTEGER, matchDegree INTEGER, tradeTime INTEGER, price INTEGER, discount INTEGER)")
        database.execSQL("CREATE TABLE smart_feature (id INTEGER PRIMARY KEY NOT NULL, userId TEXT, feature TEXT, version INTEGER DEFAULT 0 NOT NULL, createTime INTEGER, provider TEXT)")
        database.execSQL("CREATE TABLE smart_sign (id INTEGER PRIMARY KEY NOT NULL, uid TEXT, generateTime INTEGER, timeout INTEGER, scanTimeBound TEXT, seqBound TEXT, codeBound TEXT)")
    }
}

val MIGRATION_18_19 = object : Migration(18, 19) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_device ADD COLUMN 'whiteCardVersion' INTEGER DEFAULT 0 NOT NULL")
    }
}

val MIGRATION_19_20 = object : Migration(19, 20) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_device ADD COLUMN 'otherPermission' TEXT")
    }
}

val MIGRATION_20_21 = object : Migration(20, 21) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("UPDATE smart_device SET 'otherPermission' = 'FFFFFFFFFFFFFFFF' ")
    }
}

val MIGRATION_21_22 = object : Migration(21, 22) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_feature ADD COLUMN 'faceImagePath' TEXT")
        database.execSQL("ALTER TABLE smart_rule ADD COLUMN 'cardtypeBringPeopleTimeSpace' INTEGER")
    }
}

val MIGRATION_22_23 = object : Migration(22, 23) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_face ADD COLUMN 'type' TEXT")
    }
}

val MIGRATION_23_24 = object : Migration(23, 24) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_record ADD COLUMN 'temperature' TEXT")
    }
}

val MIGRATION_24_25 = object : Migration(24, 25) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_rule ADD COLUMN 'offPeakNum' INTEGER DEFAULT 1 NOT NULL")
        database.execSQL("ALTER TABLE smart_rule ADD COLUMN 'rushNum' INTEGER DEFAULT 1 NOT NULL")
    }
}

val MIGRATION_25_26 = object : Migration(25, 26) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_feature ADD COLUMN 'userName' TEXT")
    }
}

val MIGRATION_26_27 = object : Migration(26, 27) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE smart_wukong (id INTEGER PRIMARY KEY NOT NULL, code TEXT, codeEffectiveTime INTEGER, codeHead TEXT, codeCreateTime INTEGER, codeUserId TEXT, codeTicket TEXT, tradePrice INTEGER, tradeTime INTEGER, basicPrice INTEGER, discount INTEGER)")
    }
}

val MIGRATION_27_28 = object : Migration(27, 28) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_feature ADD COLUMN 'filePath' TEXT")
    }
}

val MIGRATION_28_29 = object : Migration(28, 29) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE INDEX IF NOT EXISTS index_smart_wukong_codeTicket ON smart_wukong (codeTicket)")
    }
}

val MIGRATION_29_30 = object : Migration(29, 30) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE smart_tencent_bc (id INTEGER PRIMARY KEY NOT NULL, qrcode TEXT, payfee INTEGER, scene INTEGER, scantype INTEGER, pos_id TEXT, pos_trx_id TEXT, card_id TEXT, ykt_id INTEGER, max_pay_fee INTEGER, biz_data TEXT, record TEXT, tradeTime INTEGER, basicPrice INTEGER, discount INTEGER)")
    }
}

val MIGRATION_30_31 = object : Migration(30, 31) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_feature ADD COLUMN 'updateTime' INTEGER")
    }
}

val MIGRATION_31_32 = object : Migration(31, 32) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE smart_meituan (id INTEGER PRIMARY KEY NOT NULL, basePrice INTEGER, transactAmount INTEGER, transactTime INTEGER, discount INTEGER, code TEXT, payAccountNo TEXT, userAcountNo TEXT, cardIssuerId TEXT, codeIssuerId TEXT, userAccountType TEXT, maxFee INTEGER, payType TEXT)")
    }
}

val MIGRATION_32_33 = object : Migration(32, 33) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_record ADD COLUMN 'stationName' TEXT")
        database.execSQL("ALTER TABLE smart_card ADD COLUMN 'ucCInStopName' TEXT")
    }
}

val MIGRATION_33_34 = object : Migration(33, 34) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_face ADD COLUMN 'use' TEXT")
    }
}

val MIGRATION_34_35 = object : Migration(34, 35) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_online_bankcard ADD COLUMN 'quicS2Content' TEXT")
        database.execSQL("ALTER TABLE smart_unionpaycode ADD COLUMN 'recordId' INTEGER")
        database.execSQL("ALTER TABLE smart_unionpaycode ADD COLUMN 'consumeNo' TEXT")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_smart_unionpaycode_appSign ON smart_unionpaycode (appSign)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_smart_bwtonmot_paymentAccountUserPrivateSign ON smart_bwtonmot (paymentAccountUserPrivateSign)")
    }
}

val MIGRATION_35_36 = object : Migration(35, 36) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_face ADD COLUMN 'externalUuid' TEXT")
    }
}

val MIGRATION_36_37 = object : Migration(36, 37) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE smart_tongcheng (id INTEGER PRIMARY KEY NOT NULL,basePrice INTEGER,transactAmount INTEGER,transactTime INTEGER,discount INTEGER,code TEXT,payAcc TEXT,userAcc TEXT,cardIssueNo TEXT,cardIssuePlatNo TEXT,userAccType TEXT,payLimit INTEGER,codeGenTime INTEGER,extend TEXT)")
    }
}

val MIGRATION_37_38 = object : Migration(37, 38) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_alipay_tk ADD COLUMN 'algId' INTEGER")
        database.execSQL("ALTER TABLE smart_alipay_tk ADD COLUMN 'keyId' INTEGER")
        database.execSQL("ALTER TABLE smart_alipay_tk ADD COLUMN 'cardType' TEXT")
        database.execSQL("CREATE TABLE smart_xingyun(id INTEGER PRIMARY KEY NOT NULL,deviceNoXingyun TEXT,deviceNoGmcc TEXT,mak2 TEXT,enk2 TEXT,qrCodeCertificate TEXT,sdkInitParam TEXT,sdkOperationsParam TEXT)")
    }
}

val MIGRATION_38_39 = object : Migration(38, 39) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_face ADD COLUMN 'faceImagePath' TEXT")
        database.execSQL("ALTER TABLE smart_face ADD COLUMN 'uname' TEXT")
    }
}

val MIGRATION_39_40 = object : Migration(39, 40) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_bwtonmot ADD COLUMN 'transactAmount' INTEGER")
        database.execSQL("ALTER TABLE smart_bwtonmot ADD COLUMN 'discount' INTEGER")
        database.execSQL("DROP  TABLE smart_tongcheng")
    }
}

val MIGRATION_40_41 = object : Migration(40, 41) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_card ADD COLUMN 'ucAppStartDate' TEXT")
        database.execSQL("ALTER TABLE smart_card ADD COLUMN 'ucAppEndDate' TEXT")

        database.execSQL("ALTER TABLE smart_device ADD COLUMN 'driverCardCsn' TEXT DEFAULT '00000000' NOT NULL")
        database.execSQL("ALTER TABLE smart_device ADD COLUMN 'driverWorkTime' TEXT DEFAULT '00000000' NOT NULL")
        database.execSQL("ALTER TABLE smart_device ADD COLUMN 'ticketSellerWorkState' INTEGER DEFAULT 1 NOT NULL")
        database.execSQL("ALTER TABLE smart_device ADD COLUMN 'ticketSellerCardCsn' TEXT DEFAULT '00000000' NOT NULL")
        database.execSQL("ALTER TABLE smart_device ADD COLUMN 'ticketSellerCardClass' TEXT DEFAULT '' NOT NULL")
        database.execSQL("ALTER TABLE smart_device ADD COLUMN 'ticketSellerWorkTime' TEXT DEFAULT '' NOT NULL")
        database.execSQL("ALTER TABLE smart_device ADD COLUMN 'currentField' TEXT")

        database.execSQL("ALTER TABLE smart_record ADD COLUMN 'driverCardCsn' TEXT")
        database.execSQL("ALTER TABLE smart_record ADD COLUMN 'driverCardClass' TEXT")
        database.execSQL("ALTER TABLE smart_record ADD COLUMN 'driverWorkTime' TEXT")
        database.execSQL("ALTER TABLE smart_record ADD COLUMN 'ticketSellerMainInfoBody' TEXT")
        database.execSQL("ALTER TABLE smart_record ADD COLUMN 'ticketSellerCardCsn' TEXT")
        database.execSQL("ALTER TABLE smart_record ADD COLUMN 'ticketSellerCardClass' TEXT")
        database.execSQL("ALTER TABLE smart_record ADD COLUMN 'ticketSellerWorkTime' TEXT")
        database.execSQL("ALTER TABLE smart_record ADD COLUMN 'currentField' TEXT")
    }
}

val MIGRATION_41_42 = object : Migration(41, 42) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_device ADD COLUMN 'faceDistanceLimit' FLOAT")
    }
}

val MIGRATION_42_43 = object : Migration(42, 43) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_yun ADD COLUMN 'basicPrice' INTEGER")
        database.execSQL("ALTER TABLE smart_yun ADD COLUMN 'discount' INTEGER")
        database.execSQL("ALTER TABLE smart_yun ADD COLUMN 'code' TEXT")
    }
}
val MIGRATION_43_44 = object : Migration(43, 44) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_card ADD COLUMN 'chargeDate2' TEXT")
        database.execSQL("ALTER TABLE smart_card ADD COLUMN 'mac' TEXT")
    }
}

val MIGRATION_44_45 = object : Migration(44, 45) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE smart_line ADD COLUMN 'singleMulMode' INTEGER DEFAULT 0 NOT NULL")
    }
}
