declare const uni: any

import CryptoJS from 'crypto-js'

export interface OssSettings {
  autoSyncOnStart: boolean
  syncOnRefresh: boolean
  pollingInterval: number
  conflictStrategy: string
  encryptUpload: boolean
  cacheLimit: string
  cacheExpireDays: number
}

export interface AliOssRuntimeConfig {
  endpoint: string
  bucket: string
  region: string
  accessKeyId: string
  accessKeySecret: string
  securityToken: string
  expiresAt: number
}

export interface AliOssSession {
  account: string
  bucket: string
  connectedAt: number
}

export interface InitialOssProfile {
  ownerName: string
  ownerRole: string
  babyName: string
  gender: string
  birthday: string
  monthAge: string
  bloodType: string
  healthNotes: string[]
  milestones: Array<{ title: string; date: string }>
  initializedAt: string
}

const OSS_SETTINGS_KEY = 'oss_settings'
const ALI_OSS_RUNTIME_CONFIG_KEY = 'ali_oss_runtime_config'
const ALI_OSS_SESSION_KEY = 'ali_oss_session'

const defaultAliOssRuntimeConfig: AliOssRuntimeConfig = {
  endpoint: '',
  bucket: '',
  region: '',
  accessKeyId: '',
  accessKeySecret: '',
  securityToken: '',
  expiresAt: 0
}

export const ossSettingsStorage = {
  getSettings(): OssSettings {
    try {
      const data = uni.getStorageSync(OSS_SETTINGS_KEY)
      if (data) {
        return {
          autoSyncOnStart: true,
          syncOnRefresh: true,
          pollingInterval: 10,
          conflictStrategy: 'LWW',
          encryptUpload: true,
          cacheLimit: '2 GB',
          cacheExpireDays: 7,
          ...JSON.parse(data)
        }
      }
    } catch (e) {
      console.error('Failed to get oss settings:', e)
    }
    return {
      autoSyncOnStart: true,
      syncOnRefresh: true,
      pollingInterval: 10,
      conflictStrategy: 'LWW',
      encryptUpload: true,
      cacheLimit: '2 GB',
      cacheExpireDays: 7
    }
  },

  setSettings(settings: OssSettings): boolean {
    try {
      uni.setStorageSync(OSS_SETTINGS_KEY, JSON.stringify(settings))
      return true
    } catch (e) {
      console.error('Failed to set oss settings:', e)
      return false
    }
  }
}

export const aliOssStorage = {
  getRuntimeConfig(): AliOssRuntimeConfig {
    try {
      const data = uni.getStorageSync(ALI_OSS_RUNTIME_CONFIG_KEY)
      if (data) {
        return {
          ...defaultAliOssRuntimeConfig,
          ...JSON.parse(data)
        }
      }
    } catch (e) {
      console.error('Failed to get Ali OSS runtime config:', e)
    }
    return { ...defaultAliOssRuntimeConfig }
  },

  setRuntimeConfig(config: AliOssRuntimeConfig): boolean {
    try {
      uni.setStorageSync(ALI_OSS_RUNTIME_CONFIG_KEY, JSON.stringify(config))
      return true
    } catch (e) {
      console.error('Failed to set Ali OSS runtime config:', e)
      return false
    }
  },

  getSession(): AliOssSession | null {
    try {
      const data = uni.getStorageSync(ALI_OSS_SESSION_KEY)
      if (data) {
        return JSON.parse(data)
      }
    } catch (e) {
      console.error('Failed to get Ali OSS session:', e)
    }
    return null
  },

  setSession(session: AliOssSession): boolean {
    try {
      uni.setStorageSync(ALI_OSS_SESSION_KEY, JSON.stringify(session))
      return true
    } catch (e) {
      console.error('Failed to set Ali OSS session:', e)
      return false
    }
  },

  clearSession(): void {
    try {
      uni.removeStorageSync(ALI_OSS_SESSION_KEY)
    } catch (e) {
      console.error('Failed to clear Ali OSS session:', e)
    }
  }
}

export function validateAliOssRuntimeConfig(config: AliOssRuntimeConfig): string {
  if (config.endpoint.trim().length === 0) return '请先配置 OSS endpoint'
  if (config.bucket.trim().length === 0) return '请先配置 OSS bucket'
  if (config.accessKeyId.trim().length === 0) return '请先配置 OSS AccessKeyId'
  if (config.accessKeySecret.trim().length === 0) return '请先配置 OSS AccessKeySecret'
  if (config.expiresAt > 0 && Date.now() >= config.expiresAt) return 'OSS 凭证已过期，请更新'
  return ''
}

function maskAccessKeyId(accessKeyId: string): string {
  if (accessKeyId.length <= 6) {
    return accessKeyId
  }
  return accessKeyId.substring(0, 3) + '***' + accessKeyId.substring(accessKeyId.length - 3)
}

export async function connectAliOss(): Promise<AliOssSession> {
  const config = aliOssStorage.getRuntimeConfig()
  const errorMessage = validateAliOssRuntimeConfig(config)
  if (errorMessage.length > 0) {
    throw new Error(errorMessage)
  }

  const session: AliOssSession = {
    account: maskAccessKeyId(config.accessKeyId),
    bucket: config.bucket,
    connectedAt: Date.now()
  }

  aliOssStorage.setSession(session)
  return session
}

export async function checkAliOssConnection(): Promise<AliOssSession | null> {
  const config = aliOssStorage.getRuntimeConfig()
  const errorMessage = validateAliOssRuntimeConfig(config)
  if (errorMessage.length > 0) {
    aliOssStorage.clearSession()
    return null
  }

  const session = aliOssStorage.getSession()
  if (session != null) {
    return session
  }

  return await connectAliOss()
}

function sanitizeOssEndpoint(endpoint: string, bucket: string): string {
  let host = endpoint
    .replace('https://', '')
    .replace('http://', '')
    .replace(/\/$/, '')

  const bucketPrefix = bucket.trim() + '.'
  if (bucket.trim().length > 0 && host.startsWith(bucketPrefix)) {
    host = host.substring(bucketPrefix.length)
  }

  return host
}

function normalizeObjectKey(objectKey: string): string {
  return objectKey.replace(/^\//, '')
}

function buildOssUrl(config: AliOssRuntimeConfig, objectKey: string): string {
  const host = sanitizeOssEndpoint(config.endpoint, config.bucket)
  const key = normalizeObjectKey(objectKey)
  return 'https://' + config.bucket + '.' + host + '/' + key
}

function extractOssErrorMessage(data: any): string {
  if (typeof data === 'string') {
    const codeMatch = data.match(/<Code>(.*?)<\/Code>/)
    const messageMatch = data.match(/<Message>(.*?)<\/Message>/)
    const requestIdMatch = data.match(/<RequestId>(.*?)<\/RequestId>/)
    const hostIdMatch = data.match(/<HostId>(.*?)<\/HostId>/)
    const code = codeMatch?.[1] ?? ''
    const message = messageMatch?.[1] ?? data
    const requestId = requestIdMatch?.[1] ?? ''
    const hostId = hostIdMatch?.[1] ?? ''
    let result = code.length > 0 ? code + ': ' + message : message
    if (requestId.length > 0) {
      result += ' (RequestId: ' + requestId + ')'
    }
    if (hostId.length > 0) {
      result += ' (HostId: ' + hostId + ')'
    }
    return result
  }

  if (data != null && typeof data === 'object') {
    const code = String(data.Code ?? data.code ?? '')
    const message = String(data.Message ?? data.message ?? '')
    const requestId = String(data.RequestId ?? data.requestId ?? '')
    const hostId = String(data.HostId ?? data.hostId ?? '')
    if (code.length > 0 || message.length > 0) {
      let result = (code.length > 0 ? code + ': ' : '') + message
      if (requestId.length > 0) {
        result += ' (RequestId: ' + requestId + ')'
      }
      if (hostId.length > 0) {
        result += ' (HostId: ' + hostId + ')'
      }
      return result
    }
  }

  return ''
}

function buildCanonicalizedHeaders(config: AliOssRuntimeConfig, date: string): string {
  const headers: string[] = []
  headers.push('x-oss-date:' + date)
  if (config.securityToken.trim().length > 0) {
    headers.push('x-oss-security-token:' + config.securityToken.trim())
  }
  return headers.join('\n') + '\n'
}

function buildOssAuthorization(
  method: string,
  contentType: string,
  date: string,
  canonicalizedHeaders: string,
  canonicalizedResource: string,
  accessKeyId: string,
  accessKeySecret: string
): string {
  const stringToSign = method + '\n\n' + contentType + '\n' + date + '\n' + canonicalizedHeaders + canonicalizedResource
  console.log('StringToSign:', JSON.stringify(stringToSign))
  const signature = CryptoJS.enc.Base64.stringify(CryptoJS.HmacSHA1(stringToSign, accessKeySecret))
  return 'OSS ' + accessKeyId + ':' + signature
}

function requestAsync(options: any): Promise<any> {
  return new Promise((resolve, reject) => {
    uni.request({
      ...options,
      success: (res: any) => {
        resolve(res)
      },
      fail: (err: any) => {
        reject(err)
      }
    })
  })
}

export function buildInitialOssProfile(): InitialOssProfile {
  return {
    ownerName: '爸爸',
    ownerRole: '家庭共享账号',
    babyName: '小宝',
    gender: '男',
    birthday: '2024-08-14',
    monthAge: '1岁 8个月',
    bloodType: '待补充',
    healthNotes: ['鸡蛋过敏待观察', '夜间易醒', '辅食偏爱南瓜'],
    milestones: [
      { title: '第一次翻身', date: '2025-01-10' },
      { title: '第一次独立行走', date: '2026-04-28' }
    ],
    initializedAt: new Date().toISOString()
  }
}

export async function initializeAliOssProfile(profile?: InitialOssProfile): Promise<string> {
  const config = aliOssStorage.getRuntimeConfig()
  const errorMessage = validateAliOssRuntimeConfig(config)
  if (errorMessage.length > 0) {
    throw new Error(errorMessage)
  }

  const objectKey = 'Baby/Config/profile.json'
  const body = JSON.stringify(profile ?? buildInitialOssProfile(), null, 2)
  const contentType = 'application/json'
  const date = new Date().toUTCString()
  const canonicalizedHeaders = buildCanonicalizedHeaders(config, date)
  const canonicalizedResource = '/' + config.bucket + '/' + normalizeObjectKey(objectKey)
  console.log('=== OSS Signature Debug ===')
  console.log('date:', date)
  console.log('canonicalizedHeaders:', JSON.stringify(canonicalizedHeaders))
  console.log('canonicalizedResource:', canonicalizedResource)
  const authorization = buildOssAuthorization(
    'PUT',
    contentType,
    date,
    canonicalizedHeaders,
    canonicalizedResource,
    config.accessKeyId,
    config.accessKeySecret
  )

  const header: Record<string, string> = {
    Authorization: authorization,
    'x-oss-date': date,
    'Content-Type': contentType
  }

  if (config.securityToken.trim().length > 0) {
    header['x-oss-security-token'] = config.securityToken.trim()
  }

  const response = await requestAsync({
    url: buildOssUrl(config, objectKey),
    method: 'PUT',
    header,
    data: body
  })

  const statusCode = Number(response.statusCode ?? 0)
  if (statusCode < 200 || statusCode >= 300) {
    const detail = extractOssErrorMessage(response.data)
    const fullResponse = typeof response.data === 'string' ? response.data : JSON.stringify(response.data, null, 2)
    console.error('OSS Error Response:', fullResponse)
    throw new Error('初始化 OSS 个人信息失败，状态码: ' + statusCode.toString() + (detail.length > 0 ? '，原因: ' + detail : ''))
  }

  return objectKey
}
