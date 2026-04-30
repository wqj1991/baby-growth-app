export interface RecordItemSettings {
  feeding: boolean
  potty: boolean
  sleep: boolean
  growth: boolean
  healthMore: boolean
  temperature: boolean
  medication: boolean
  vaccine: boolean
  symptom: boolean
  visit: boolean
  customEvent: boolean
}

export interface AppConfig {
  version: string
  recordItemSettings: RecordItemSettings
}

const CONFIG_VERSION = '1.0.0'

const defaultRecordItemSettings: RecordItemSettings = {
  feeding: true,
  potty: true,
  sleep: true,
  growth: true,
  healthMore: true,
  temperature: true,
  medication: true,
  vaccine: true,
  symptom: true,
  visit: true,
  customEvent: true
}

const defaultAppConfig: AppConfig = {
  version: CONFIG_VERSION,
  recordItemSettings: { ...defaultRecordItemSettings }
}

const STORAGE_KEY = 'baobao_app_config'

function migrateConfig(stored: AppConfig): AppConfig {
  const config = { ...defaultAppConfig, ...stored }
  
  if (stored.version !== CONFIG_VERSION) {
    console.warn(`Config version mismatch: stored=${stored.version}, current=${CONFIG_VERSION}`)
    config.version = CONFIG_VERSION
    
    if (!stored.recordItemSettings) {
      config.recordItemSettings = { ...defaultRecordItemSettings }
    } else {
      config.recordItemSettings = { ...defaultRecordItemSettings, ...stored.recordItemSettings }
    }
  }
  
  return config
}

function getConfig(): AppConfig {
  try {
    const stored = uni.getStorageSync(STORAGE_KEY)
    if (stored) {
      const parsed = JSON.parse(stored)
      return migrateConfig(parsed)
    }
  } catch (e) {
    console.error('Failed to get config:', e)
    uni.removeStorageSync(STORAGE_KEY)
  }
  return { ...defaultAppConfig }
}

function saveConfig(config: AppConfig): boolean {
  try {
    uni.setStorageSync(STORAGE_KEY, JSON.stringify(config))
    return true
  } catch (e) {
    console.error('Failed to save config:', e)
    return false
  }
}

const appConfig = getConfig()

export const recordItemStore = {
  settings: { ...appConfig.recordItemSettings },

  toggleItem(key: keyof RecordItemSettings): boolean {
    this.settings[key] = !this.settings[key]
    return this.save()
  },

  setItem(key: keyof RecordItemSettings, value: boolean): boolean {
    this.settings[key] = value
    return this.save()
  },

  getItem(key: keyof RecordItemSettings): boolean {
    return this.settings[key]
  },

  getBasicItems(): Record<keyof Pick<RecordItemSettings, 'feeding' | 'potty' | 'sleep' | 'growth' | 'healthMore'>, boolean> {
    return {
      feeding: this.settings.feeding,
      potty: this.settings.potty,
      sleep: this.settings.sleep,
      growth: this.settings.growth,
      healthMore: this.settings.healthMore
    }
  },

  getHealthSubItems(): Record<keyof Pick<RecordItemSettings, 'temperature' | 'medication' | 'vaccine' | 'symptom' | 'visit' | 'customEvent'>, boolean> {
    return {
      temperature: this.settings.temperature,
      medication: this.settings.medication,
      vaccine: this.settings.vaccine,
      symptom: this.settings.symptom,
      visit: this.settings.visit,
      customEvent: this.settings.customEvent
    }
  },

  getEnabledBasicCount(): number {
    return Object.values(this.getBasicItems()).filter(Boolean).length
  },

  getEnabledHealthCount(): number {
    return Object.values(this.getHealthSubItems()).filter(Boolean).length
  },

  resetToDefault(): boolean {
    this.settings = { ...defaultRecordItemSettings }
    return this.save()
  },

  save(): boolean {
    appConfig.recordItemSettings = { ...this.settings }
    return saveConfig(appConfig)
  },

  getConfigVersion(): string {
    return appConfig.version
  }
}

export function clearAllConfig(): boolean {
  try {
    uni.removeStorageSync(STORAGE_KEY)
    return true
  } catch (e) {
    console.error('Failed to clear config:', e)
    return false
  }
}
