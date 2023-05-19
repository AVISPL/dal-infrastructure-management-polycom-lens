/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.polycom.lens.common;

/**
 * PolyLensRequest contain the queries
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 4/11/2023
 * @since 1.0.0
 */
public class PolyLensQueries {
	public static final String SYSTEM_INFO = "{\"query\":\"query getPoly {\\n  countDevices\\n  calculateQueryCost {\\n    queryCost\\n    costUsed\\n    costRemaining\\n    secondsToReset\\n  }\\n  tenantCount\\n  tenants {\\n    id\\n    name\\n    type\\n    memberCount\\n }\\n}\"}";
	public static final String AGGREGATED_DEVICES = "{\"query\":\"query allDevices($params: DeviceFindArgs) {\\n  deviceSearch(params: $params) {\\n    edges {\\n      node {\\n        id\\n        supportsSettings\\n        supportsSoftwareUpdate\\n        callStatus\\n        tags\\n        etag\\n        name\\n        tenantId\\n        productId\\n        organization\\n        manufacturer\\n        hardwareFamily\\n        hardwareModel\\n        hardwareRevision\\n        softwareVersion\\n        softwareBuild\\n        externalIp\\n        internalIp\\n        macAddress\\n        serialNumber\\n        connected\\n        activeApplicationName\\n        activeApplicationVersion\\n        provisioningEnabled\\n        lastConfigRequestDate\\n        lastDetected\\n        shipmentDate\\n        hardwareProduct\\n        proxyAgent\\n        proxyAgentId\\n        proxyAgentVersion\\n        usbVendorId\\n        usbProductId\\n        dateRegistered\\n        hasPeripherals\\n        allPeripheralsLinked\\n        inVirtualDevice\\n        user {\\n          name\\n        }\\n        room {\\n          name\\n        }\\n        model {\\n          name\\n          description\\n          hardwareFamily {\\n            name\\n          }\\n          hardwareManufacturer {\\n            name\\n          }\\n        }\\n        site {\\n          name\\n        }\\n        systemStatus {\\n          data {\\n            com {\\n              poly {\\n                device {\\n                  status {\\n                    provisioning {\\n                      state\\n                    }\\n                    globaldirectory {\\n                      state\\n                    }\\n                    ipnetwork {\\n                      state\\n                    }\\n                    trackablecamera {\\n                      state\\n                   }\\n                   camera {\\n                      state\\n                   }\\n                   audio {\\n                      state\\n                   }\\n                   remotecontrol {\\n                     state\\n                   }\\n                   logthreshold {\\n                     state\\n                   }\\n                 }\\n               }\\n             }\\n           }\\n          }\\n        }\\n        connections {\\n          name\\n          macAddress\\n          softwareVersion\\n        }\\n        location {\\n          coordinate {\\n            latitude\\n            longitude\\n          }\\n        }\\n        entitlements {\\n          productSerial\\n          licenseKey\\n          date\\n          endDate\\n          expired\\n        }\\n        bandwidth {\\n          endTime\\n          downloadMbps\\n          pingJitterMs\\n          pingLatencyMs\\n          pingLossPercent\\n          uploadMbps\\n        } \\n      }\\n    }\\n    pageInfo {\\n      totalCount\\n      countOnPage\\n      nextToken\\n      hasNextPage\\n    }\\n  }\\n}\",$variables}";
	public static final String REBOOT_DEVICE = "{\"query\":\"mutation RebootDevice($deviceId: String!) {\\n  rebootDevice(deviceId: $deviceId) {\\n    success\\n    error\\n  }\\n}\",\"variables\":{\"deviceId\":\"$DeviceId\"}}";
}
