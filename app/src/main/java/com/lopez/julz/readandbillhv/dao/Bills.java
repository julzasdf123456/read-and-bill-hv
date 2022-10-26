package com.lopez.julz.readandbillhv.dao;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Bills {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "BillNumber")
    private String BillNumber;

    @ColumnInfo(name = "AccountNumber")
    private String AccountNumber;

    @ColumnInfo(name = "ServicePeriod")
    private String ServicePeriod;

    @ColumnInfo(name = "Multiplier")
    private String Multiplier;

    @ColumnInfo(name = "Coreloss")
    private String Coreloss;

    @ColumnInfo(name = "KwhUsed")
    private String KwhUsed;

    @ColumnInfo(name = "PreviousKwh")
    private String PreviousKwh;

    @ColumnInfo(name = "PresentKwh")
    private String PresentKwh;

    @ColumnInfo(name = "DemandPreviousKwh")
    private String DemandPreviousKwh;

    @ColumnInfo(name = "DemandPresentKwh")
    private String DemandPresentKwh;

    @ColumnInfo(name = "AdditionalKwh")
    private String AdditionalKwh;

    @ColumnInfo(name = "AdditionalDemandKwh")
    private String AdditionalDemandKwh;

    @ColumnInfo(name = "KwhAmount")
    private String KwhAmount;

    @ColumnInfo(name = "EffectiveRate")
    private String EffectiveRate;

    @ColumnInfo(name = "AdditionalCharges")
    private String AdditionalCharges;

    @ColumnInfo(name = "Deductions")
    private String Deductions;

    @ColumnInfo(name = "NetAmount")
    private String NetAmount;

    @ColumnInfo(name = "BillingDate")
    private String BillingDate;

    @ColumnInfo(name = "ServiceDateFrom")
    private String ServiceDateFrom;

    @ColumnInfo(name = "ServiceDateTo")
    private String ServiceDateTo;

    @ColumnInfo(name = "DueDate")
    private String DueDate;

    @ColumnInfo(name = "MeterNumber")
    private String MeterNumber;

    @ColumnInfo(name = "ConsumerType")
    private String ConsumerType;

    @ColumnInfo(name = "BillType")
    private String BillType;

    /**
     * RATES
     */
    @ColumnInfo(name = "GenerationSystemCharge")
    private String GenerationSystemCharge;

    @ColumnInfo(name = "TransmissionDeliveryChargeKW")
    private String TransmissionDeliveryChargeKW;

    @ColumnInfo(name = "TransmissionDeliveryChargeKWH")
    private String TransmissionDeliveryChargeKWH;

    @ColumnInfo(name = "SystemLossCharge")
    private String SystemLossCharge;

    @ColumnInfo(name = "DistributionDemandCharge")
    private String DistributionDemandCharge;

    @ColumnInfo(name = "DistributionSystemCharge")
    private String DistributionSystemCharge;

    @ColumnInfo(name = "SupplyRetailCustomerCharge")
    private String SupplyRetailCustomerCharge;

    @ColumnInfo(name = "SupplySystemCharge")
    private String SupplySystemCharge;

    @ColumnInfo(name = "MeteringRetailCustomerCharge")
    private String MeteringRetailCustomerCharge;

    @ColumnInfo(name = "MeteringSystemCharge")
    private String MeteringSystemCharge;

    @ColumnInfo(name = "RFSC")
    private String RFSC;

    @ColumnInfo(name = "LifelineRate")
    private String LifelineRate;

    @ColumnInfo(name = "InterClassCrossSubsidyCharge")
    private String InterClassCrossSubsidyCharge;

    @ColumnInfo(name = "PPARefund")
    private String PPARefund;

    @ColumnInfo(name = "SeniorCitizenSubsidy")
    private String SeniorCitizenSubsidy;

    @ColumnInfo(name = "MissionaryElectrificationCharge")
    private String MissionaryElectrificationCharge;

    @ColumnInfo(name = "EnvironmentalCharge")
    private String EnvironmentalCharge;

    @ColumnInfo(name = "StrandedContractCosts")
    private String StrandedContractCosts;

    @ColumnInfo(name = "NPCStrandedDebt")
    private String NPCStrandedDebt;

    @ColumnInfo(name = "FeedInTariffAllowance")
    private String FeedInTariffAllowance;

    @ColumnInfo(name = "MissionaryElectrificationREDCI")
    private String MissionaryElectrificationREDCI;

    @ColumnInfo(name = "GenerationVAT")
    private String GenerationVAT;

    @ColumnInfo(name = "TransmissionVAT")
    private String TransmissionVAT;

    @ColumnInfo(name = "SystemLossVAT")
    private String SystemLossVAT;

    @ColumnInfo(name = "DistributionVAT")
    private String DistributionVAT;

    @ColumnInfo(name = "RealPropertyTax")
    private String RealPropertyTax;

    @ColumnInfo(name = "OtherGenerationRateAdjustment")
    private String OtherGenerationRateAdjustment;

    @ColumnInfo(name = "OtherTransmissionCostAdjustmentKW")
    private String OtherTransmissionCostAdjustmentKW;

    @ColumnInfo(name = "OtherTransmissionCostAdjustmentKWH")
    private String OtherTransmissionCostAdjustmentKWH;

    @ColumnInfo(name = "OtherSystemLossCostAdjustment")
    private String OtherSystemLossCostAdjustment;

    @ColumnInfo(name = "OtherLifelineRateCostAdjustment")
    private String OtherLifelineRateCostAdjustment;

    @ColumnInfo(name = "SeniorCitizenDiscountAndSubsidyAdjustment")
    private String SeniorCitizenDiscountAndSubsidyAdjustment;

    @ColumnInfo(name = "FranchiseTax")
    private String FranchiseTax;

    @ColumnInfo(name = "BusinessTax")
    private String BusinessTax;

    @ColumnInfo(name = "Notes")
    private String Notes;

    @ColumnInfo(name = "UserId")
    private String UserId;

    @ColumnInfo(name = "BilledFrom")
    private String BilledFrom;

    @ColumnInfo(name = "UploadStatus")
    private String UploadStatus;

    @ColumnInfo(name = "DeductedDeposit")
    private String DeductedDeposit;

    @ColumnInfo(name = "ExcessDeposit")
    private String ExcessDeposit;

    @ColumnInfo(name = "Evat2Percent")
    private String Evat2Percent;

    @ColumnInfo(name = "Evat5Percent")
    private String Evat5Percent;

    @ColumnInfo(name = "KatasNgVat")
    private String KatasNgVat;

    @ColumnInfo(name = "SolarImportPresent")
    private String SolarImportPresent;

    @ColumnInfo(name = "SolarImportPrevious")
    private String SolarImportPrevious;

    @ColumnInfo(name = "SolarExportPresent")
    private String SolarExportPresent;

    @ColumnInfo(name = "SolarExportPrevious")
    private String SolarExportPrevious;

    @ColumnInfo(name = "SolarImportKwh")
    private String SolarImportKwh;

    @ColumnInfo(name = "SolarExportKwh")
    private String SolarExportKwh;

    @ColumnInfo(name = "GenerationChargeSolarExport")
    private String GenerationChargeSolarExport;

    @ColumnInfo(name = "SolarResidualCredit")
    private String SolarResidualCredit;

    @ColumnInfo(name = "SolarDemandChargeKW")
    private String SolarDemandChargeKW;

    @ColumnInfo(name = "SolarDemandChargeKWH")
    private String SolarDemandChargeKWH;

    @ColumnInfo(name = "SolarRetailCustomerCharge")
    private String SolarRetailCustomerCharge;

    @ColumnInfo(name = "SolarSupplySystemCharge")
    private String SolarSupplySystemCharge;

    @ColumnInfo(name = "SolarMeteringRetailCharge")
    private String SolarMeteringRetailCharge;

    @ColumnInfo(name = "SolarMeteringSystemCharge")
    private String SolarMeteringSystemCharge;

    @ColumnInfo(name = "Item1")
    private String Item1;

    @ColumnInfo(name = "Item2")
    private String Item2;

    @ColumnInfo(name = "Item3")
    private String Item3;

    @ColumnInfo(name = "Item4")
    private String Item4;

    @ColumnInfo(name = "Item5")
    private String Item5;


    public Bills() {
    }

    public Bills(@NonNull String id, String billNumber, String accountNumber, String servicePeriod, String multiplier, String coreloss, String kwhUsed, String previousKwh, String presentKwh, String demandPreviousKwh, String demandPresentKwh, String additionalKwh, String additionalDemandKwh, String kwhAmount, String effectiveRate, String additionalCharges, String deductions, String netAmount, String billingDate, String serviceDateFrom, String serviceDateTo, String dueDate, String meterNumber, String consumerType, String billType, String generationSystemCharge, String transmissionDeliveryChargeKW, String transmissionDeliveryChargeKWH, String systemLossCharge, String distributionDemandCharge, String distributionSystemCharge, String supplyRetailCustomerCharge, String supplySystemCharge, String meteringRetailCustomerCharge, String meteringSystemCharge, String RFSC, String lifelineRate, String interClassCrossSubsidyCharge, String PPARefund, String seniorCitizenSubsidy, String missionaryElectrificationCharge, String environmentalCharge, String strandedContractCosts, String NPCStrandedDebt, String feedInTariffAllowance, String missionaryElectrificationREDCI, String generationVAT, String transmissionVAT, String systemLossVAT, String distributionVAT, String realPropertyTax, String otherGenerationRateAdjustment, String otherTransmissionCostAdjustmentKW, String otherTransmissionCostAdjustmentKWH, String otherSystemLossCostAdjustment, String otherLifelineRateCostAdjustment, String seniorCitizenDiscountAndSubsidyAdjustment, String franchiseTax, String businessTax, String notes, String userId, String billedFrom, String uploadStatus, String deductedDeposit, String excessDeposit, String evat2Percent, String evat5Percent, String katasNgVat, String solarImportPresent, String solarImportPrevious, String solarExportPresent, String solarExportPrevious, String solarImportKwh, String solarExportKwh, String generationChargeSolarExport, String solarResidualCredit, String solarDemandChargeKW, String solarDemandChargeKWH, String solarRetailCustomerCharge, String solarSupplySystemCharge, String solarMeteringRetailCharge, String solarMeteringSystemCharge, String item1, String item2, String item3, String item4, String item5) {
        this.id = id;
        BillNumber = billNumber;
        AccountNumber = accountNumber;
        ServicePeriod = servicePeriod;
        Multiplier = multiplier;
        Coreloss = coreloss;
        KwhUsed = kwhUsed;
        PreviousKwh = previousKwh;
        PresentKwh = presentKwh;
        DemandPreviousKwh = demandPreviousKwh;
        DemandPresentKwh = demandPresentKwh;
        AdditionalKwh = additionalKwh;
        AdditionalDemandKwh = additionalDemandKwh;
        KwhAmount = kwhAmount;
        EffectiveRate = effectiveRate;
        AdditionalCharges = additionalCharges;
        Deductions = deductions;
        NetAmount = netAmount;
        BillingDate = billingDate;
        ServiceDateFrom = serviceDateFrom;
        ServiceDateTo = serviceDateTo;
        DueDate = dueDate;
        MeterNumber = meterNumber;
        ConsumerType = consumerType;
        BillType = billType;
        GenerationSystemCharge = generationSystemCharge;
        TransmissionDeliveryChargeKW = transmissionDeliveryChargeKW;
        TransmissionDeliveryChargeKWH = transmissionDeliveryChargeKWH;
        SystemLossCharge = systemLossCharge;
        DistributionDemandCharge = distributionDemandCharge;
        DistributionSystemCharge = distributionSystemCharge;
        SupplyRetailCustomerCharge = supplyRetailCustomerCharge;
        SupplySystemCharge = supplySystemCharge;
        MeteringRetailCustomerCharge = meteringRetailCustomerCharge;
        MeteringSystemCharge = meteringSystemCharge;
        this.RFSC = RFSC;
        LifelineRate = lifelineRate;
        InterClassCrossSubsidyCharge = interClassCrossSubsidyCharge;
        this.PPARefund = PPARefund;
        SeniorCitizenSubsidy = seniorCitizenSubsidy;
        MissionaryElectrificationCharge = missionaryElectrificationCharge;
        EnvironmentalCharge = environmentalCharge;
        StrandedContractCosts = strandedContractCosts;
        this.NPCStrandedDebt = NPCStrandedDebt;
        FeedInTariffAllowance = feedInTariffAllowance;
        MissionaryElectrificationREDCI = missionaryElectrificationREDCI;
        GenerationVAT = generationVAT;
        TransmissionVAT = transmissionVAT;
        SystemLossVAT = systemLossVAT;
        DistributionVAT = distributionVAT;
        RealPropertyTax = realPropertyTax;
        OtherGenerationRateAdjustment = otherGenerationRateAdjustment;
        OtherTransmissionCostAdjustmentKW = otherTransmissionCostAdjustmentKW;
        OtherTransmissionCostAdjustmentKWH = otherTransmissionCostAdjustmentKWH;
        OtherSystemLossCostAdjustment = otherSystemLossCostAdjustment;
        OtherLifelineRateCostAdjustment = otherLifelineRateCostAdjustment;
        SeniorCitizenDiscountAndSubsidyAdjustment = seniorCitizenDiscountAndSubsidyAdjustment;
        FranchiseTax = franchiseTax;
        BusinessTax = businessTax;
        Notes = notes;
        UserId = userId;
        BilledFrom = billedFrom;
        UploadStatus = uploadStatus;
        DeductedDeposit = deductedDeposit;
        ExcessDeposit = excessDeposit;
        Evat2Percent = evat2Percent;
        Evat5Percent = evat5Percent;
        KatasNgVat = katasNgVat;
        SolarImportPresent = solarImportPresent;
        SolarImportPrevious = solarImportPrevious;
        SolarExportPresent = solarExportPresent;
        SolarExportPrevious = solarExportPrevious;
        SolarImportKwh = solarImportKwh;
        SolarExportKwh = solarExportKwh;
        GenerationChargeSolarExport = generationChargeSolarExport;
        SolarResidualCredit = solarResidualCredit;
        SolarDemandChargeKW = solarDemandChargeKW;
        SolarDemandChargeKWH = solarDemandChargeKWH;
        SolarRetailCustomerCharge = solarRetailCustomerCharge;
        SolarSupplySystemCharge = solarSupplySystemCharge;
        SolarMeteringRetailCharge = solarMeteringRetailCharge;
        SolarMeteringSystemCharge = solarMeteringSystemCharge;
        Item1 = item1;
        Item2 = item2;
        Item3 = item3;
        Item4 = item4;
        Item5 = item5;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setBillNumber(String billNumber) {
        BillNumber = billNumber;
    }

    public void setAccountNumber(String accountNumber) {
        AccountNumber = accountNumber;
    }

    public void setServicePeriod(String servicePeriod) {
        ServicePeriod = servicePeriod;
    }

    public void setMultiplier(String multiplier) {
        Multiplier = multiplier;
    }

    public void setCoreloss(String coreloss) {
        Coreloss = coreloss;
    }

    public void setKwhUsed(String kwhUsed) {
        KwhUsed = kwhUsed;
    }

    public void setPreviousKwh(String previousKwh) {
        PreviousKwh = previousKwh;
    }

    public void setPresentKwh(String presentKwh) {
        PresentKwh = presentKwh;
    }

    public void setDemandPreviousKwh(String demandPreviousKwh) {
        DemandPreviousKwh = demandPreviousKwh;
    }

    public void setDemandPresentKwh(String demandPresentKwh) {
        DemandPresentKwh = demandPresentKwh;
    }

    public void setAdditionalKwh(String additionalKwh) {
        AdditionalKwh = additionalKwh;
    }

    public void setAdditionalDemandKwh(String additionalDemandKwh) {
        AdditionalDemandKwh = additionalDemandKwh;
    }

    public void setKwhAmount(String kwhAmount) {
        KwhAmount = kwhAmount;
    }

    public void setEffectiveRate(String effectiveRate) {
        EffectiveRate = effectiveRate;
    }

    public void setAdditionalCharges(String additionalCharges) {
        AdditionalCharges = additionalCharges;
    }

    public void setDeductions(String deductions) {
        Deductions = deductions;
    }

    public void setNetAmount(String netAmount) {
        NetAmount = netAmount;
    }

    public void setBillingDate(String billingDate) {
        BillingDate = billingDate;
    }

    public void setServiceDateFrom(String serviceDateFrom) {
        ServiceDateFrom = serviceDateFrom;
    }

    public void setServiceDateTo(String serviceDateTo) {
        ServiceDateTo = serviceDateTo;
    }

    public void setDueDate(String dueDate) {
        DueDate = dueDate;
    }

    public void setMeterNumber(String meterNumber) {
        MeterNumber = meterNumber;
    }

    public void setConsumerType(String consumerType) {
        ConsumerType = consumerType;
    }

    public void setBillType(String billType) {
        BillType = billType;
    }

    public void setGenerationSystemCharge(String generationSystemCharge) {
        GenerationSystemCharge = generationSystemCharge;
    }

    public void setTransmissionDeliveryChargeKW(String transmissionDeliveryChargeKW) {
        TransmissionDeliveryChargeKW = transmissionDeliveryChargeKW;
    }

    public void setTransmissionDeliveryChargeKWH(String transmissionDeliveryChargeKWH) {
        TransmissionDeliveryChargeKWH = transmissionDeliveryChargeKWH;
    }

    public void setSystemLossCharge(String systemLossCharge) {
        SystemLossCharge = systemLossCharge;
    }

    public void setDistributionDemandCharge(String distributionDemandCharge) {
        DistributionDemandCharge = distributionDemandCharge;
    }

    public void setDistributionSystemCharge(String distributionSystemCharge) {
        DistributionSystemCharge = distributionSystemCharge;
    }

    public void setSupplyRetailCustomerCharge(String supplyRetailCustomerCharge) {
        SupplyRetailCustomerCharge = supplyRetailCustomerCharge;
    }

    public void setSupplySystemCharge(String supplySystemCharge) {
        SupplySystemCharge = supplySystemCharge;
    }

    public void setMeteringRetailCustomerCharge(String meteringRetailCustomerCharge) {
        MeteringRetailCustomerCharge = meteringRetailCustomerCharge;
    }

    public void setMeteringSystemCharge(String meteringSystemCharge) {
        MeteringSystemCharge = meteringSystemCharge;
    }

    public void setRFSC(String RFSC) {
        this.RFSC = RFSC;
    }

    public void setLifelineRate(String lifelineRate) {
        LifelineRate = lifelineRate;
    }

    public void setInterClassCrossSubsidyCharge(String interClassCrossSubsidyCharge) {
        InterClassCrossSubsidyCharge = interClassCrossSubsidyCharge;
    }

    public void setPPARefund(String PPARefund) {
        this.PPARefund = PPARefund;
    }

    public void setSeniorCitizenSubsidy(String seniorCitizenSubsidy) {
        SeniorCitizenSubsidy = seniorCitizenSubsidy;
    }

    public void setMissionaryElectrificationCharge(String missionaryElectrificationCharge) {
        MissionaryElectrificationCharge = missionaryElectrificationCharge;
    }

    public void setEnvironmentalCharge(String environmentalCharge) {
        EnvironmentalCharge = environmentalCharge;
    }

    public void setStrandedContractCosts(String strandedContractCosts) {
        StrandedContractCosts = strandedContractCosts;
    }

    public void setNPCStrandedDebt(String NPCStrandedDebt) {
        this.NPCStrandedDebt = NPCStrandedDebt;
    }

    public void setFeedInTariffAllowance(String feedInTariffAllowance) {
        FeedInTariffAllowance = feedInTariffAllowance;
    }

    public void setMissionaryElectrificationREDCI(String missionaryElectrificationREDCI) {
        MissionaryElectrificationREDCI = missionaryElectrificationREDCI;
    }

    public void setGenerationVAT(String generationVAT) {
        GenerationVAT = generationVAT;
    }

    public void setTransmissionVAT(String transmissionVAT) {
        TransmissionVAT = transmissionVAT;
    }

    public void setSystemLossVAT(String systemLossVAT) {
        SystemLossVAT = systemLossVAT;
    }

    public void setDistributionVAT(String distributionVAT) {
        DistributionVAT = distributionVAT;
    }

    public void setRealPropertyTax(String realPropertyTax) {
        RealPropertyTax = realPropertyTax;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setBilledFrom(String billedFrom) {
        BilledFrom = billedFrom;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getBillNumber() {
        return BillNumber;
    }

    public String getAccountNumber() {
        return AccountNumber;
    }

    public String getServicePeriod() {
        return ServicePeriod;
    }

    public String getMultiplier() {
        return Multiplier;
    }

    public String getCoreloss() {
        return Coreloss;
    }

    public String getKwhUsed() {
        return KwhUsed;
    }

    public String getPreviousKwh() {
        return PreviousKwh;
    }

    public String getPresentKwh() {
        return PresentKwh;
    }

    public String getDemandPreviousKwh() {
        return DemandPreviousKwh;
    }

    public String getDemandPresentKwh() {
        return DemandPresentKwh;
    }

    public String getAdditionalKwh() {
        return AdditionalKwh;
    }

    public String getAdditionalDemandKwh() {
        return AdditionalDemandKwh;
    }

    public String getKwhAmount() {
        return KwhAmount;
    }

    public String getEffectiveRate() {
        return EffectiveRate;
    }

    public String getAdditionalCharges() {
        return AdditionalCharges;
    }

    public String getDeductions() {
        return Deductions;
    }

    public String getNetAmount() {
        return NetAmount;
    }

    public String getBillingDate() {
        return BillingDate;
    }

    public String getServiceDateFrom() {
        return ServiceDateFrom;
    }

    public String getServiceDateTo() {
        return ServiceDateTo;
    }

    public String getDueDate() {
        return DueDate;
    }

    public String getMeterNumber() {
        return MeterNumber;
    }

    public String getConsumerType() {
        return ConsumerType;
    }

    public String getBillType() {
        return BillType;
    }

    public String getGenerationSystemCharge() {
        return GenerationSystemCharge;
    }

    public String getTransmissionDeliveryChargeKW() {
        return TransmissionDeliveryChargeKW;
    }

    public String getTransmissionDeliveryChargeKWH() {
        return TransmissionDeliveryChargeKWH;
    }

    public String getSystemLossCharge() {
        return SystemLossCharge;
    }

    public String getDistributionDemandCharge() {
        return DistributionDemandCharge;
    }

    public String getDistributionSystemCharge() {
        return DistributionSystemCharge;
    }

    public String getSupplyRetailCustomerCharge() {
        return SupplyRetailCustomerCharge;
    }

    public String getSupplySystemCharge() {
        return SupplySystemCharge;
    }

    public String getMeteringRetailCustomerCharge() {
        return MeteringRetailCustomerCharge;
    }

    public String getMeteringSystemCharge() {
        return MeteringSystemCharge;
    }

    public String getRFSC() {
        return RFSC;
    }

    public String getLifelineRate() {
        return LifelineRate;
    }

    public String getInterClassCrossSubsidyCharge() {
        return InterClassCrossSubsidyCharge;
    }

    public String getPPARefund() {
        return PPARefund;
    }

    public String getSeniorCitizenSubsidy() {
        return SeniorCitizenSubsidy;
    }

    public String getMissionaryElectrificationCharge() {
        return MissionaryElectrificationCharge;
    }

    public String getEnvironmentalCharge() {
        return EnvironmentalCharge;
    }

    public String getStrandedContractCosts() {
        return StrandedContractCosts;
    }

    public String getNPCStrandedDebt() {
        return NPCStrandedDebt;
    }

    public String getFeedInTariffAllowance() {
        return FeedInTariffAllowance;
    }

    public String getMissionaryElectrificationREDCI() {
        return MissionaryElectrificationREDCI;
    }

    public String getGenerationVAT() {
        return GenerationVAT;
    }

    public String getTransmissionVAT() {
        return TransmissionVAT;
    }

    public String getSystemLossVAT() {
        return SystemLossVAT;
    }

    public String getDistributionVAT() {
        return DistributionVAT;
    }

    public String getRealPropertyTax() {
        return RealPropertyTax;
    }

    public String getNotes() {
        return Notes;
    }

    public String getUserId() {
        return UserId;
    }

    public String getBilledFrom() {
        return BilledFrom;
    }

    public String getUploadStatus() {
        return UploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        UploadStatus = uploadStatus;
    }

    public String getOtherGenerationRateAdjustment() {
        return OtherGenerationRateAdjustment;
    }

    public void setOtherGenerationRateAdjustment(String otherGenerationRateAdjustment) {
        OtherGenerationRateAdjustment = otherGenerationRateAdjustment;
    }

    public String getOtherTransmissionCostAdjustmentKW() {
        return OtherTransmissionCostAdjustmentKW;
    }

    public void setOtherTransmissionCostAdjustmentKW(String otherTransmissionCostAdjustmentKW) {
        OtherTransmissionCostAdjustmentKW = otherTransmissionCostAdjustmentKW;
    }

    public String getOtherTransmissionCostAdjustmentKWH() {
        return OtherTransmissionCostAdjustmentKWH;
    }

    public void setOtherTransmissionCostAdjustmentKWH(String otherTransmissionCostAdjustmentKWH) {
        OtherTransmissionCostAdjustmentKWH = otherTransmissionCostAdjustmentKWH;
    }

    public String getOtherSystemLossCostAdjustment() {
        return OtherSystemLossCostAdjustment;
    }

    public void setOtherSystemLossCostAdjustment(String otherSystemLossCostAdjustment) {
        OtherSystemLossCostAdjustment = otherSystemLossCostAdjustment;
    }

    public String getOtherLifelineRateCostAdjustment() {
        return OtherLifelineRateCostAdjustment;
    }

    public void setOtherLifelineRateCostAdjustment(String otherLifelineRateCostAdjustment) {
        OtherLifelineRateCostAdjustment = otherLifelineRateCostAdjustment;
    }

    public String getSeniorCitizenDiscountAndSubsidyAdjustment() {
        return SeniorCitizenDiscountAndSubsidyAdjustment;
    }

    public void setSeniorCitizenDiscountAndSubsidyAdjustment(String seniorCitizenDiscountAndSubsidyAdjustment) {
        SeniorCitizenDiscountAndSubsidyAdjustment = seniorCitizenDiscountAndSubsidyAdjustment;
    }

    public String getFranchiseTax() {
        return FranchiseTax;
    }

    public void setFranchiseTax(String franchiseTax) {
        FranchiseTax = franchiseTax;
    }

    public String getBusinessTax() {
        return BusinessTax;
    }

    public void setBusinessTax(String businessTax) {
        BusinessTax = businessTax;
    }

    public String getDeductedDeposit() {
        return DeductedDeposit;
    }

    public void setDeductedDeposit(String deductedDeposit) {
        DeductedDeposit = deductedDeposit;
    }

    public String getExcessDeposit() {
        return ExcessDeposit;
    }

    public void setExcessDeposit(String excessDeposit) {
        ExcessDeposit = excessDeposit;
    }

    public String getEvat2Percent() {
        return Evat2Percent;
    }

    public void setEvat2Percent(String evat2Percent) {
        Evat2Percent = evat2Percent;
    }

    public String getEvat5Percent() {
        return Evat5Percent;
    }

    public void setEvat5Percent(String evat5Percent) {
        Evat5Percent = evat5Percent;
    }

    public String getKatasNgVat() {
        return KatasNgVat;
    }

    public void setKatasNgVat(String katasNgVat) {
        KatasNgVat = katasNgVat;
    }

    public String getSolarImportPresent() {
        return SolarImportPresent;
    }

    public void setSolarImportPresent(String solarImportPresent) {
        SolarImportPresent = solarImportPresent;
    }

    public String getSolarImportPrevious() {
        return SolarImportPrevious;
    }

    public void setSolarImportPrevious(String solarImportPrevious) {
        SolarImportPrevious = solarImportPrevious;
    }

    public String getSolarExportPresent() {
        return SolarExportPresent;
    }

    public void setSolarExportPresent(String solarExportPresent) {
        SolarExportPresent = solarExportPresent;
    }

    public String getSolarExportPrevious() {
        return SolarExportPrevious;
    }

    public void setSolarExportPrevious(String solarExportPrevious) {
        SolarExportPrevious = solarExportPrevious;
    }

    public String getSolarImportKwh() {
        return SolarImportKwh;
    }

    public void setSolarImportKwh(String solarImportKwh) {
        SolarImportKwh = solarImportKwh;
    }

    public String getSolarExportKwh() {
        return SolarExportKwh;
    }

    public void setSolarExportKwh(String solarExportKwh) {
        SolarExportKwh = solarExportKwh;
    }

    public String getGenerationChargeSolarExport() {
        return GenerationChargeSolarExport;
    }

    public void setGenerationChargeSolarExport(String generationChargeSolarExport) {
        GenerationChargeSolarExport = generationChargeSolarExport;
    }

    public String getSolarResidualCredit() {
        return SolarResidualCredit;
    }

    public void setSolarResidualCredit(String solarResidualCredit) {
        SolarResidualCredit = solarResidualCredit;
    }

    public String getSolarDemandChargeKW() {
        return SolarDemandChargeKW;
    }

    public void setSolarDemandChargeKW(String solarDemandChargeKW) {
        SolarDemandChargeKW = solarDemandChargeKW;
    }

    public String getSolarDemandChargeKWH() {
        return SolarDemandChargeKWH;
    }

    public void setSolarDemandChargeKWH(String solarDemandChargeKWH) {
        SolarDemandChargeKWH = solarDemandChargeKWH;
    }

    public String getSolarRetailCustomerCharge() {
        return SolarRetailCustomerCharge;
    }

    public void setSolarRetailCustomerCharge(String solarRetailCustomerCharge) {
        SolarRetailCustomerCharge = solarRetailCustomerCharge;
    }

    public String getSolarSupplySystemCharge() {
        return SolarSupplySystemCharge;
    }

    public void setSolarSupplySystemCharge(String solarSupplySystemCharge) {
        SolarSupplySystemCharge = solarSupplySystemCharge;
    }

    public String getSolarMeteringRetailCharge() {
        return SolarMeteringRetailCharge;
    }

    public void setSolarMeteringRetailCharge(String solarMeteringRetailCharge) {
        SolarMeteringRetailCharge = solarMeteringRetailCharge;
    }

    public String getSolarMeteringSystemCharge() {
        return SolarMeteringSystemCharge;
    }

    public void setSolarMeteringSystemCharge(String solarMeteringSystemCharge) {
        SolarMeteringSystemCharge = solarMeteringSystemCharge;
    }

    public String getItem1() {
        return Item1;
    }

    public void setItem1(String item1) {
        Item1 = item1;
    }

    public String getItem2() {
        return Item2;
    }

    public void setItem2(String item2) {
        Item2 = item2;
    }

    public String getItem3() {
        return Item3;
    }

    public void setItem3(String item3) {
        Item3 = item3;
    }

    public String getItem4() {
        return Item4;
    }

    public void setItem4(String item4) {
        Item4 = item4;
    }

    public String getItem5() {
        return Item5;
    }

    public void setItem5(String item5) {
        Item5 = item5;
    }
}
