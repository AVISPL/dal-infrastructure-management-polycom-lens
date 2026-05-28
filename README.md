# Poly Lens Integration - Capabilities & Configuration
This document covers Poly Lens Aggregator Capabilities and Configuration.

Symphony integrates with Poly Lens to provide centralized monitoring and management of Poly collaboration devices.
Main features are: real-time device health monitoring, bandwidth and network quality monitoring, inventory visibility, linked peripheral tracking, system status monitoring, and device control capabilities.

Poly Lens provides a centralized dashboard for monitoring Poly devices such as conference phones, video conferencing systems, and peripherals.

## Main use cases
- **Monitor** Poly device health, connectivity, operational status and bandwidth/network quality metrics
- **Track** individual device details - serial number, MAC address, software version, peripherals, room and site information
- **Inventory** all Poly devices registered to the Poly Lens tenant
- **Control** supported devices through available actions such as Reboot Device
- **Filter** monitored devices by model, room, or site

## Prerequisites and where to start
Poly Lens Aggregator communicates with the Poly Lens Cloud API using HTTPS connectivity.

Before integrating Poly Lens with Symphony, the following prerequisites must be completed:
- Active Poly Lens tenant/account
- Poly devices registered and visible within Poly Lens
- Client ID and Client Secret credentials generated from Poly Lens

The Symphony instance or Cloud Connector must be able to reach:
- Management address: 'api.silica-prod01.io.lens.poly.com' (DNS endpoint may vary)
- HTTPS / TCP Port: 443

Firewall or proxy rules must allow outbound HTTPS connectivity for communication between the Symphony Cloud Connector to the Poly Lens cloud endpoint.

## Poly Lens Device Configuration and Provisioning

The Poly Lens Aggregator device must be configured in Symphony with the following values:

| Field | Value |
|---|---|
| Device Type | Infrastructure |
| Category | Management |
| Manufacturer | Poly |
| Model | Lens (Monitoring Proxy) |
| Monitoring Service | Advanced Monitoring |
| Monitoring Source | Direct |
| Protocol | HTTPS |
| Username | Client ID |
| Password | Client Secret |
| Port Number | 443 |
| Management Address | Example: api.silica-prod01.io.lens.poly.com (DNS endpoint may vary and may change over time) |

Supported Models: All Poly devices, supported by the Poly Lens App.

The aggregated Poly devices will be available as aggregated devices with different models.

When the device is configured, saved and set active, Poly Lens Aggregator will start communicating with the Poly Lens API to retrieve data about registered devices, based on the provided configuration.
By default, the unprovisioned devices will appear on Aggregated Devices -> Unprovisioned Devices tab.

To import a Poly Lens aggregated device for monitoring by the Poly Lens Aggregator:
1. Open Aggregated Devices
2. Select unprovisioned devices
3. Fill required provisioning fields
4. Import devices into Symphony

Required provisioning values:

| Field | Value |
|---|---|
| Type | Codecs |
| Category | Single Codecs |
| Manufacturer | Poly |
| Supported Protocol | Dual H.323 & SIP |
| Calling Method | SIP IP |

Note: If Supported Protocol or Calling Method are not provided, the device will not import successfully into Symphony.

For detailed information on aggregator and its configuration, please refer to our knowledgebase -> https://symphony.knowledgeowl.com/help/poly-lens-aggregator-technical-breakdown

Devices and available device data can be tuned by adapter configuration properties.

## Filtering Device(s) and Component(s)

| Property | Description | Value |
|---|---|---|
| filterModelName | Filter devices by Poly device model name | CSV/String |
| filterRoomName | Filter devices by room name | CSV/String |
| filterSiteName | Filter devices by site name | CSV/String |
| filterExcludeRoomName | Exclude devices by room name | CSV/String |

Note: When filtering devices, it is recommended to export the CSV device list from the Poly Lens dashboard and use it to validate filters and monitoring scope.

## Available Monitored Data
Poly Lens Aggregator monitored data consists of 2 parts: Aggregator extended properties and Aggregated Device extended properties.

Available aggregator properties include:

| Property Type | Description |
|---|---|
| Adapter Metadata | General Adapter information -> AdapterBuildDate, AdapterUptime, AdapterVersion, LastMonitoringCycleDuration, MonitoredDevicesTotal, MonitoringCycleInterval |
| Tenant Information | TenantCount, TenantID, TenantMemberCount, TenantName, TenantType |
| QueryCost Group | QueryCost Group is used to determine the cost of executing API queries. CostRemaining value resets to 100K entries every 60 seconds. -> CostRemaining, CostUsed, QueryCost, SecondsToReset |

Aggregated Devices provide the following monitoring and control capabilities:

| Property Type | Description |
|---|---|
| Device Properties | General Poly device information -> device identity, application details, registration details, device online status, IP addressing, hardware details, MAC address, room/site assignment, proxy agent details, software information, tenant information, supported capabilities, and user association |
| Device Status | Device operational state information -> call status, provisioning status, peripheral availability, linked peripheral status, virtual device state, settings support, and software update support |
| BandWidth | Network and bandwidth monitoring statistics -> upload/download throughput, ping latency, jitter, packet loss, and bandwidth measurement timestamps |
| Entitlements | Licensing and entitlement information -> entitlement dates, expiration status, license keys, and associated product serial information |
| LinkedDevice | Linked peripheral device information -> linked device MAC address, device name, and software version |
| Location | Device geographic information -> latitude and longitude coordinates |
| Model | Device hardware and manufacturer information -> model description, hardware family, hardware manufacturer, and model name |
| SystemStatus | Device subsystem monitoring -> built-in camera status, camera availability, microphone status, LAN network status, provisioning service status, remote control status, and global directory status |
| Controls | Supported device control capabilities -> Reboot Device action |

Supported control capabilities:

| Control | Description |
|---|---|
| Reboot Device | Reboot supported Poly devices |

Note: When aggregated device is offline, RebootDevice button will disappear.
Monitoring and control capabilities may depend on the device model.

## Troubleshooting
** Login Error **
- Verify Client ID and Client Secret are correct
- Verify HTTPS configuration
- Verify Monitoring Service is set to Advanced Monitoring and Monitoring Source is set to Direct

** API Error **
- Check API error description
- Verify Poly Lens cloud hostname configuration and device filtering properties
- Verify QueryCost group values if monitoring data is incomplete

** Link Error/Ping Timeout **
- Verify Cloud Connector connectivity to the management address.
- Verify outbound HTTPS connectivity & firewall/proxy configuration

If none of the recommended steps help, please enter an SOS ticket at {https://avi-spl.atlassian.net/servicedesk/customer/portals}

## What AI Assistant can do with it:
- Find and Monitor Poly Lens Aggregated Devices (Poly Lens Aggregator as Monitoring Proxy)
- Verify Poly Lens Aggregator configuration
- Track linked peripherals and associated devices

## What AI Assistant cannot do with it:
- Provision devices automatically
- Monitor unsupported Poly device models
- Guarantee identical monitoring capabilities across all Poly devices
- Execute unsupported device control actions