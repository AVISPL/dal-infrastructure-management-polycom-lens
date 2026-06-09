# Poly Lens Integration - Capabilities & Configuration
This document covers Poly Lens Aggregator Capabilities and Configuration.

Symphony integrates with Poly Lens to provide centralized monitoring and management of Poly collaboration devices.
Poly Lens provides a centralized dashboard for monitoring Poly devices such as conference phones, video conferencing systems, and peripherals.

## Main use cases for Poly Lens Integration
- **Monitor** Poly device health, connectivity, operational status and bandwidth/network quality metrics
- **Track** individual device details - serial number, MAC address, software version, peripherals, room and site information
- **Inventory** all Poly devices registered to the Poly Lens tenant
- **Control** supported devices through available actions such as Reboot Device
- **Filter** monitored devices by model, room, or site

## Prerequisites for the Poly Lens Connection Setup 
Poly Lens Aggregator communicates with the Poly Lens Cloud API using HTTPS connectivity.

Note: The prerequisites below describe the requirements for a successful Poly Lens integration setup. They are not to be infered as troubleshooting checks and should not be used when diagnosing specific errors unless a troubleshooting entry (provided in the Troubleshooting section) explicitly references them.

Before integrating Poly Lens with Symphony, the following prerequisites must be completed:
- Active Poly Lens tenant/account
- Poly devices registered and visible within Poly Lens
- Client ID and Client Secret credentials generated from Poly Lens

Firewall or proxy rules must allow outbound HTTPS connectivity for communication between the Symphony Cloud Connector to the Poly Lens cloud endpoint.

## Poly Lens Connection Setup and Provisioning

The Poly Lens Aggregator connection must be setup in Symphony with the following values:

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
| Management Address | Poly Lens API hostname. Example: api.silica-prod01.io.lens.poly.com. The actual hostname may vary by Poly Lens environment and is subject to change. |

Supported Models: All Poly devices, supported by the Poly Lens App.

The aggregated Poly devices will be available as aggregated devices with different models.

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

**Adapter configuration properties** - For filtering Device(s) and component(s)

| Property | Description |
| --- | --- |
| filterModelName | Filter devices by Poly device model name |
| filterRoomName | Filter devices by room name |
| filterSiteName | Filter devices by site name |
| filterExcludeRoomName | Exclude devices by room name |

Note: When filtering devices, it is recommended to export the CSV device list from the Poly Lens dashboard and use it to validate filters and monitoring scope.

## Available Monitored Data for Poly Devices
Poly Lens Aggregator monitored data consists of 2 parts: Aggregator extended properties and Aggregated Device extended properties.

Available aggregator properties include:

| Property Type | Description |
|---|---|
| Adapter Metadata | General Adapter information -> AdapterBuildDate, AdapterUptime, AdapterVersion, LastMonitoringCycleDuration, MonitoredDevicesTotal, MonitoringCycleInterval |
| Tenant Information | TenantCount, TenantID, TenantMemberCount, TenantName, TenantType |
| QueryCost Group | QueryCost Group is used to determine the cost of executing API queries. CostRemaining value resets to 100K entries every 60 seconds. -> CostRemaining, CostUsed, QueryCost, SecondsToReset |

Poly Lens aggreagtor exposes these property groups for the aggregated devices:

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

Note: When aggregated device is offline, RebootDevice button will disappear. Monitoring and control capabilities may depend on the device model.

## Troubleshooting for Poly Lens Integration

**Troubleshooting guidance**
- If an error occurs, focus only on troubleshooting steps that are provided in the section below.
- Do not include prerequisite/setup information.
- Do not add unrelated configuration details from other sections.
- If the document does not provide a direct error troubleshooting step, state that the document does not contain enough guidance for that specific issue.

**Login Error**
- Verify Client ID and Client Secret are correct
- Verify HTTPS configuration

**API Error**
- Check API error description
- Verify Poly Lens cloud hostname configuration and device filtering properties
- Verify QueryCost group values if monitoring data is incomplete

**Link Error/Ping Timeout**
- Verify Cloud Connector connectivity to the management address.
- Verify outbound HTTPS connectivity & firewall/proxy configuration

If none of the recommended steps help, please enter an SOS ticket at {https://avi-spl.atlassian.net/servicedesk/customer/portals}

## What AI Assistant can do with the Poly Lens Integration:
- Find and Monitor Poly Lens Aggregated Devices (Poly Lens Aggregator as Monitoring Proxy)
- Verify Poly Lens Aggregator configuration
- Track linked peripherals and associated devices

## What AI Assistant cannot do with the Poly Lens Integration:
- Provision devices automatically
- Monitor unsupported Poly device models
- Guarantee identical monitoring capabilities across all Poly devices
- Execute unsupported device control actions