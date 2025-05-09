declare namespace WorkCollaborativeImagesAPI {
  type BaseResponseBoolean = {
    code?: number
    message?: string
    data?: boolean
  }

  type BaseResponseListPicture = {
    code?: number
    message?: string
    data?: Picture[]
  }

  type BaseResponseListPictureVO = {
    code?: number
    message?: string
    data?: PictureVO[]
  }

  type BaseResponseListString = {
    code?: number
    message?: string
    data?: string[]
  }

  type BaseResponsePageUser = {
    code?: number
    message?: string
    data?: PageUser
  }

  type BaseResponsePicture = {
    code?: number
    message?: string
    data?: Picture
  }

  type BaseResponsePictureVO = {
    code?: number
    message?: string
    data?: PictureVO
  }

  type BaseResponseUser = {
    code?: number
    message?: string
    data?: User
  }

  type BaseResponseUserStatus = {
    code?: number
    message?: string
    data?: UserStatus
  }

  type BaseResponseUserVO = {
    code?: number
    message?: string
    data?: UserVO
  }

  type OrderItem = {
    column?: string
    asc?: boolean
  }

  type PageUser = {
    records?: User[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PageUser
    searchCount?: PageUser
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
    pages?: number
  }

  type Picture = {
    id?: number
    url?: string
    name?: string
    introduction?: string
    category?: string
    tags?: string
    picSize?: number
    picWidth?: number
    picHeight?: number
    picScale?: number
    picFormat?: string
    userId?: number
    deleted?: number
    createTime?: string
    updateTime?: string
  }

  type PictureSearchRequest = {
    pageCurrent?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    name?: string
    introduction?: string
    category?: string
    tags?: string
    picSize?: number
    picWidth?: number
    picHeight?: number
    picScale?: number
    picFormat?: string
    userId?: number
  }

  type pictureUploadParams = {
    pictureId?: number
    pictureCategory?: string
    pictureIntroduction?: string
    pictureTags?: string[]
  }

  type pictureUploadVOParams = {
    pictureId?: number
    pictureCategory?: string
    pictureIntroduction?: string
    pictureTags?: string[]
  }

  type PictureVO = {
    id?: number
    url?: string
    name?: string
    introduction?: string
    category?: string
    tags?: string
    picSize?: number
    picWidth?: number
    picHeight?: number
    picScale?: number
    picFormat?: string
    userId?: number
    createTime?: string
    updateTime?: string
    userVO?: UserVO
  }

  type User = {
    id?: number
    account?: string
    wxUnion?: string
    mpOpen?: string
    email?: string
    phone?: string
    ident?: string
    passwd?: string
    avatar?: string
    tags?: string
    nick?: string
    name?: string
    profile?: string
    birthday?: string
    country?: string
    address?: string
    role?: number
    level?: number
    gender?: number
    deleted?: number
    createTime?: string
    updateTime?: string
  }

  type UserAddRequest = {
    account?: string
    wxUnion?: string
    mpOpen?: string
    email?: string
    phone?: string
    ident?: string
    passwd?: string
    avatar?: string
    tags?: string
    nick?: string
    name?: string
    profile?: string
    birthday?: string
    country?: string
    address?: string
    role?: number
    level?: number
    gender?: number
  }

  type UserDeleteRequest = {
    id?: number
  }

  type UserDisableRequest = {
    id?: number
    disableTime?: number
  }

  type UserLoginRequest = {
    account?: string
    passwd?: string
  }

  type UserRegisterRequest = {
    account?: string
    passwd?: string
    checkPasswd?: string
  }

  type UserSearchRequest = {
    pageCurrent?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    account?: string
    tags?: string
    nick?: string
    name?: string
    profile?: string
    address?: string
    role?: number
    level?: number
  }

  type UserStatus = {
    isLogin?: boolean
    tokenName?: string
    tokenTimeout?: string
    userId?: string
    userRole?: number
  }

  type UserUpdateRequest = {
    id?: number
    account?: string
    wxUnion?: string
    mpOpen?: string
    email?: string
    phone?: string
    ident?: string
    passwd?: string
    avatar?: string
    tags?: string
    nick?: string
    name?: string
    profile?: string
    birthday?: string
    country?: string
    address?: string
    role?: number
    level?: number
    gender?: number
  }

  type UserVO = {
    id?: number
    account?: string
    wxUnion?: string
    mpOpen?: string
    email?: string
    phone?: string
    ident?: string
    avatar?: string
    tags?: string
    nick?: string
    name?: string
    profile?: string
    birthday?: string
    country?: string
    address?: string
    role?: number
    level?: number
    gender?: number
  }
}
