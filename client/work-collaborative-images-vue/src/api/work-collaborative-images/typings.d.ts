declare namespace WorkCollaborativeImagesAPI {
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

  type BaseResponsePageSpaceVO = {
    code?: number
    message?: string
    data?: PageSpaceVO
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

  type PageSpaceVO = {
    records?: SpaceVO[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PageSpaceVO
    searchCount?: PageSpaceVO
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
  }

  type PictureBatchRequest = {
    searchText?: string
    searchCount?: number
    namePrefix?: string
    category?: string
  }

  type PictureDeleteRequest = {
    id?: number
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
    spaceName?: string
    spaceLevel?: number
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
    spaceName?: string
    spaceLevel?: number
  }

  type SpaceDeleteRequest = true

  type SpaceSearchRequest = {
    pageCurrent?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    userId?: number
    spaceName?: string
    spaceLevel?: number
  }

  type SpaceUpdateRequest = {
    id?: number
    spaceName?: string
    spaceLevel?: number
    maxSize?: number
    maxCount?: number
  }

  type SpaceVO = {
    id?: number
    spaceName?: string
    spaceLevel?: number
    maxSize?: number
    maxCount?: number
    totalSize?: number
    totalCount?: number
    userId?: number
    createTime?: string
    updateTime?: string
    user?: UserVO
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
