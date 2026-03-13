export interface Device {
  id: string
  deviceId: string
  name: string
  model: string
  manufacturer: string
  androidVersion: string
  apiLevel: number
  status: 'ACTIVE' | 'PENDING' | 'SUSPENDED' | 'RETIRED'
  lastSeen: string
  enrolledAt: string
  createdAt: string
  updatedAt: string
  properties?: Record<string, string>
}

export type CommandType = 'LOCK' | 'UNLOCK' | 'REBOOT' | 'INSTALL_APP' | 'UNINSTALL_APP' | 'UPDATE_CONFIG'

export interface Command {
  id: string
  type: CommandType
  status: 'PENDING' | 'SENT' | 'DELIVERED' | 'COMPLETED' | 'FAILED'
  parameters?: Record<string, any>
  result?: Record<string, any>
  createdAt: string
  completedAt?: string
}

export interface DashboardStats {
  totalDevices: number
  activeDevices: number
  pendingDevices: number
  suspendedDevices: number
  retiredDevices: number
  recentCommands: number
  failedCommands: number
}
