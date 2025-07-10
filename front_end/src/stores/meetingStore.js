import { defineStore } from 'pinia'

export const useMeetingStore = defineStore('meeting', {
  state: () => ({
    meetings: [],
    currentPage: 1,
    pageSize: 5,
    totalItems: 0
  }),
  actions: {
    setMeetings(meetings) {
      this.meetings = meetings
    },
    setPagination(pageInfo) {
      this.currentPage = pageInfo.pageNum
      this.pageSize = pageInfo.pageSize
      this.totalItems = pageInfo.totalSize
    },
    getMeetingById(id) {
      return this.meetings.find(meeting => meeting.id === id)
    }
  }
})