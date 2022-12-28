<script lang=ts>
    import { getEventByKey, IEvent } from "@/store/events";
    import { TabGroup, TabList, TabPanels, TabPanel } from "@headlessui/vue";

    export default ({
        computed: {
            searchQuery () {
                return this.$route.query.query || ""
            }
        },
        watch: {
            async searchQuery () {
                this.searching = true;
                this.eventList = await getEventByKey(this.searchQuery)
                this.ok = this.eventList.length > 0;
                this.searching = false;
            }
        },
        async mounted () {
            this.searching = true;
            this.eventList = await getEventByKey(this.searchQuery)
            this.ok = this.eventList.length > 0;
            this.searching = false;
        },
        data() {
            return {
                eventList: [],
                ok: false,
                searching: false
            }
        }
    })
</script>

<template>
    <MainLayout>
        <main class="py-13 md:ml-6">
            <div class="sticky z-30 h-20 md:container top-13 lg:h-24">
                <TabList class="tablist">
                    <SearchInput :query="searchQuery" />
                    <MobileSearch />
                </TabList>
            </div>
            <div class="md:container">
                <div v-if="ok">
                    <BrowseResult :eventList="eventList" />
                </div>
                <div v-else-if="!searching">
                    <h1 style="font-size: 30px;">Aucun résultat trouvé pour le mot-clé : <span class="italic" style="color:red" >{{searchQuery}}</span></h1>
                </div>
            </div>
        </main>
    </MainLayout>
</template>

<style scoped>
    .buttonspace {
        justify-content: space-evenly;
        padding: 0 2vw;
    }
    .item-center {
    display: grid;
    gap: 4px;
    align-items: center;
    justify-items: center;
    }
    .page-heading {
    /* text-transform: uppercase; */
    /* letter-spacing: 0.1em; */
    font-size: xx-large;
    font-weight: 150;
    }
    p.italic {
       font-style: italic;
    }

    
    .tablist {
        @apply flex items-center justify-between h-full px-5 border-b bg-epic-black/90 backdrop-blur md:px-0 border-white/10 md:border-transparent;
    }
</style>