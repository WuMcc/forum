import { defineStore } from "pinia";
import axios from "axios";

export const useStore = defineStore('general', {
    state: () => {
        return {
            user: {
                id: -1,
                username: '',
                email: '',
                role: '',
                avatar: null,
                registerTime: null
            },
            forum: {
                types: []
            }
        }
    }, getters: {
        avatarUrl() {
            let str = this.user.email;
            str = str.split('@')
            if(this.user.avatar)
                return `${axios.defaults.baseURL}/images${this.user.avatar}`
            else if(this.user.email)
                return `https://tenapi.cn/v2/qqimg?qq=${str[0]}`
            else
                return 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

        }
    }, actions: {
        findTypeById(id) {
            for (let type of this.forum.types) {
                if(type.id === id)
                    return type
            }
        },
        avatarUserUrl(avatar, email) {
            let str = email;
            str = str.split('@')
            if(avatar)
                return `${axios.defaults.baseURL}/images${avatar}`
            else if(email)
                return `https://tenapi.cn/v2/qqimg?qq=${str[0]}`
            else
                return 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
        }
    }
})
