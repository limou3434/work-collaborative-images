declare namespace WorkCollaborativeImagesAPI {
  type adminUserGetTokenParams = {
    userGetTokenRequest: UserGetTokenRequest
  }

  type BaseResponseBoolean = {
    code?: number
    message?: string
    data?: boolean
  }

  type BaseResponseInteger = {
    code?: number
    message?: string
    data?: number
  }

  type BaseResponseListSpaceLevelInfo = {
    code?: number
    message?: string
    data?: SpaceLevelInfo[]
  }

  type BaseResponseListString = {
    code?: number
    message?: string
    data?: string[]
  }

  type BaseResponsePagePicture = {
    code?: number
    message?: string
    data?: PagePicture
  }

  type BaseResponsePagePictureVO = {
    code?: number
    message?: string
    data?: PagePictureVO
  }

  type BaseResponsePageSpace = {
    code?: number
    message?: string
    data?: PageSpace
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

  type BaseResponseSpace = {
    code?: number
    message?: string
    data?: Space
  }

  type BaseResponseSpaceVO = {
    code?: number
    message?: string
    data?: SpaceVO
  }

  type BaseResponseUser = {
    code?: number
    message?: string
    data?: User
  }

  type BaseResponseUserTokenStatus = {
    code?: number
    message?: string
    data?: UserTokenStatus
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

  type PagePicture = {
    records?: Picture[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PagePicture
    searchCount?: PagePicture
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
    pages?: number
  }

  type PagePictureVO = {
    records?: PictureVO[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PagePictureVO
    searchCount?: PagePictureVO
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
    pages?: number
  }

  type PageSpace = {
    records?: Space[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PageSpace
    searchCount?: PageSpace
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
    pages?: number
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
    thumbnailUrl?: string
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
    spaceId?: number
    reviewStatus?: number
    reviewMessage?: string
    reviewTime?: string
    reviewerId?: number
    deleted?: number
    createTime?: string
    updateTime?: string
  }

  type PictureAddRequest = {
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
    spaceId?: number
    reviewStatus?: number
    reviewMessage?: string
  }

  type PictureBatchRequest = {
    searchText?: string
    searchCount?: number
    name?: string
    introduction?: string
    category?: string
    tags?: string
  }

  type PictureDeleteRequest = {
    id?: number
  }

  type PictureDestroyRequest = {
    id?: number
  }

  type PictureQueryRequest = {
    pageCurrent?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    spaceId?: number
    name?: string
    introduction?: string
    category?: string
    tags?: string
    picSize?: number
    picWidth?: number
    picHeight?: number
    picScale?: number
    picFormat?: string
  }

  type PictureReviewRequest = {
    id?: number
    reviewStatus?: number
    reviewMessage?: string
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
    spaceId?: number
    reviewStatus?: number
    reviewMessage?: string
    reviewerId?: number
  }

  type PictureUpdateRequest = {
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
  }

  type pictureUploadParams = {
    pictureId?: number
    spaceId?: number
    spaceType?: number
    pictureCategory?: string
    pictureName?: string
    pictureIntroduction?: string
    pictureTags?: string
    pictureFileUrl?: string
  }

  type PictureVO = {
    id?: number
    url?: string
    thumbnailUrl?: string
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
    spaceId?: number
    createTime?: string
    updateTime?: string
    userVO?: UserVO
    reviewStatus?: number
    reviewMessage?: string
    reviewerId?: number
  }

  type Space = {
    id?: number
    type?: number
    name?: string
    level?: number
    maxSize?: number
    maxCount?: number
    totalSize?: number
    totalCount?: number
    userId?: number
    deleted?: number
    createTime?: string
    updateTime?: string
  }

  type SpaceAddRequest = {
    name?: string
    type?: number
    level?: number
    userId?: number
  }

  type SpaceCreateSelfRequest = {
    name?: string
  }

  type SpaceDeleteRequest = {
    id?: number
  }

  type SpaceEditRequestSelf = {
    id?: number
    name?: string
  }

  type SpaceLevelInfo = {
    value?: number
    text?: string
    maxCount?: number
    maxSize?: number
  }

  type SpaceSearchRequest = {
    pageCurrent?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    userId?: number
    name?: string
    level?: number
  }

  type SpaceUpdateRequest = {
    id?: number
    name?: string
    level?: number
    maxSize?: number
    maxCount?: number
  }

  type SpaceVO = {
    id?: number
    name?: string
    level?: number
    maxSize?: number
    maxCount?: number
    totalSize?: number
    totalCount?: number
    userId?: number
    createTime?: string
    updateTime?: string
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

  type UserGetTokenRequest = {
    userId?: number
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
    email?: string
    phone?: string
    tags?: string
    nick?: string
    name?: string
    profile?: string
    address?: string
    role?: number
    level?: number
  }

  type UserTokenStatus = {
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
