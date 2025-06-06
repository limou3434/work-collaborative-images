declare namespace WorkCollaborativeImagesAPI {
  type adminUserGetTokenParams = {
    userGetTokenRequest: UserGetTokenRequest
  }

  type BaseResponseBoolean = {
    code?: number
    message?: string
    data?: boolean
  }

  type BaseResponseCreateOutPaintingTaskResponse = {
    code?: number
    message?: string
    data?: CreateOutPaintingTaskResponse
  }

  type BaseResponseGetOutPaintingTaskResponse = {
    code?: number
    message?: string
    data?: GetOutPaintingTaskResponse
  }

  type BaseResponseInteger = {
    code?: number
    message?: string
    data?: number
  }

  type BaseResponseListImageSearchResult = {
    code?: number
    message?: string
    data?: ImageSearchResult[]
  }

  type BaseResponseListPictureVO = {
    code?: number
    message?: string
    data?: PictureVO[]
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

  type BaseResponsePageSpaceUser = {
    code?: number
    message?: string
    data?: PageSpaceUser
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

  type BaseResponsePageUserVO = {
    code?: number
    message?: string
    data?: PageUserVO
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

  type BaseResponseSpaceUser = {
    code?: number
    message?: string
    data?: SpaceUser
  }

  type BaseResponseSpaceUserVO = {
    code?: number
    message?: string
    data?: SpaceUserVO
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

  type CreateOutPaintingTaskResponse = {
    code?: string
    message?: string
    requestId?: string
    output?: Output
  }

  type GetOutPaintingTaskResponse = {
    requestId?: string
    output?: Output
  }

  type ImageSearchResult = {
    thumbUrl?: string
    fromUrl?: string
  }

  type OrderItem = {
    column?: string
    asc?: boolean
  }

  type Output = {
    taskId?: string
    taskStatus?: string
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

  type PageSpaceUser = {
    records?: SpaceUser[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PageSpaceUser
    searchCount?: PageSpaceUser
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

  type PageUserVO = {
    records?: UserVO[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PageUserVO
    searchCount?: PageUserVO
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
    pages?: number
  }

  type Parameters = {
    angle?: number
    outputRatio?: string
    topOffset?: number
    bottomOffset?: number
    leftOffset?: number
    rightOffset?: number
    bestQuality?: boolean
    limitImageSize?: boolean
    addWatermark?: boolean
    xScale?: number
    yScale?: number
  }

  type Picture = {
    id?: number
    url?: string
    thumbnailUrl?: string
    originalUrl?: string
    name?: string
    introduction?: string
    category?: string
    tags?: string
    picSize?: number
    picColor?: string
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

  type PictureCreateOutPaintingTaskRequest = {
    pictureId?: number
    parameters?: Parameters
  }

  type PictureDeleteRequest = {
    id?: number
  }

  type PictureDestroyRequest = {
    id?: number
  }

  type pictureOutPaintingGetTaskParams = {
    taskId: string
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
    startEditTime?: string
    endEditTime?: string
  }

  type PictureReviewRequest = {
    id?: number
    reviewStatus?: number
    reviewMessage?: string
  }

  type PictureSearchColorRequest = {
    pageCurrent?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    pictureId?: number
  }

  type PictureSearchPictureRequest = {
    pageCurrent?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    pictureId?: number
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
    startEditTime?: string
    endEditTime?: string
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

  type SpaceCreateRequest = {
    spaceName?: string
    spaceType?: number
  }

  type SpaceDeleteRequest = {
    id?: number
  }

  type SpaceDestroyRequest = {
    spaceType?: number
  }

  type SpaceEditRequest = {
    id?: number
    name?: string
  }

  type SpaceLevelInfo = {
    value?: number
    text?: string
    maxCount?: number
    maxSize?: number
  }

  type SpaceQueryRequest = {
    spaceType?: number
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
    type?: number
  }

  type SpaceUpdateRequest = {
    id?: number
    name?: string
    level?: number
    maxSize?: number
    maxCount?: number
  }

  type SpaceUser = {
    id?: number
    spaceId?: number
    userId?: number
    spaceRole?: number
    createTime?: string
    updateTime?: string
  }

  type SpaceUserAddRequest = {
    spaceId?: number
    userId?: number
    spaceRole?: number
  }

  type SpaceUserDeleteRequest = {
    id?: number
  }

  type SpaceUserEditRequest = {
    userId?: number
    spaceId?: number
    spaceRole?: number
  }

  type spaceUserGetUserParams = {
    userId: number
  }

  type SpaceUserMoveInRequest = {
    userId?: number
    spaceId?: number
    spaceRole?: number
  }

  type SpaceUserMoveOutRequest = {
    userId?: number
    spaceId?: number
  }

  type SpaceUserSearchRequest = {
    pageCurrent?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    spaceId?: number
    userId?: number
  }

  type SpaceUserUpdateRequest = {
    id?: number
    spaceId?: number
    userId?: number
    spaceRole?: number
  }

  type SpaceUserVO = {
    id?: number
    spaceId?: number
    userId?: number
    spaceRole?: number
    createTime?: string
    updateTime?: string
    userVO?: UserVO
    spaceVO?: SpaceVO
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
    spaceRole?: number
    gender?: number
  }
}
