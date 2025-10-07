import { defineStore } from 'pinia'
import { ref,computed } from 'vue'

export const useMemberStore = defineStore('member', {
  state: () => ({
    members: []
  }),
  actions: {
    setMembers(newMembers:any) {
      this.members = newMembers
    }
  }
})
