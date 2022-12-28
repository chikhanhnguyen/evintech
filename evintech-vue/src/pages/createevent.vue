<script setup lang="ts">
    import { ref, computed, onMounted } from "vue";
    import { useRouter } from 'vue-router';
    import useRefValidation from "@/hooks/useRefValidation";
    import { adminCreateNewEvent, getAllCategories } from "@/store/events";
    import { getSavedCategories, refreshFilters } from "@/store/filter";
    import Datepicker from '@vuepic/vue-datepicker';
    //
    import { Filter, FilterValue } from "@/data/filter";
    import { getFilters, setFilters } from "@/store/filter";

    const [ eventName, setEventName, eventNameError ] = useRefValidation()
    const [ category, setCategory, categoryError ] = useRefValidation()
    const [ description, setDescription, descriptionError ] = useRefValidation()
    const [ price, setPrice, priceError ] = useRefValidation()
    // date
    const date  = ref("")
    // onMounted(() => {
    //     const startDate = new Date();
    //     const endDate = new Date(new Date().setDate(startDate.getDate() + 2));
    //     date.value = [startDate, endDate];
    // })
    
    const validInput = computed(() => 
        !!(eventName.value && category.value && description.value && selectedImageUrl.value && date.value[0] && date.value[1] )
    );

    const errMsg = ref();

    const router = useRouter();

    const isCreating = ref(false);
    
    const createNewEvent = async () => {
        isCreating.value = true;
        let errorMsg = await adminCreateNewEvent(
            {
                eventName: eventName.value,
                category: category.value,
                description: description.value,
                imageFile: selectedImageFile.value,
                price: Number(price.value),
                dateEvent: date.value[0],
                dateCloseEvent: date.value[1]
            });
        if (errorMsg != "") {
            errMsg.value = errorMsg;
        } else if (eventName.value) {
            // go to event page
            router.push("/event/" + eventName.value + "?isNew=true");
        }
        isCreating.value = false;
    }

    // image
    const selectedImageUrl = ref("");
    const selectedImageFile = ref(null);
    const onImageFileSelected = (event: any) => {
        const file = event.target.files[0];
        selectedImageFile.value = file;
        selectedImageUrl.value = URL.createObjectURL(file);
    }
    // quick code to update category list if needed
    const updateFilters = async () => {
        await refreshFilters();
    }
    updateFilters();
    // category
    const categories = getSavedCategories();
    console.log(categories);
</script>

<template>
    <MainLayout>
        <main class="py-13 md:ml-6">
            <div class="flex md:container items-center justify-center w-full py-10 text-sm select-none">
                <div class="flex flex-col md:flex-row h-[32rem] sm:h-[28rem] w-full" style="align-items: center">
                    <!-- Partie Gauche de la page -->
                    <div class="w-full md:w-[75%] h-full pl-5 md:pl-0 pr-5 md:pr-0 pb-8 md:pb-0">
                        <!-- Image de l'évenement -->
                        <div class="relative w-full h-full bg-center bg-cover rounded-2xl"
                            :style="{ backgroundImage: `url(${selectedImageUrl})` }">
                            <div class="absolute inset-0 rounded-2xl bg-gradient-to-t md:bg-gradient-to-r from-black/80 md:from-black/40 to-transparent"></div>
                        </div>

                        <!-- Descriptif -->
                        <div class="relative w-full" style="margin-top:10px">
                            <!-- <input id="descriptif" type="textfield" class="border-white/30 focus:border-white appearance-none floating-input peer" required>
                            <label for="descriptif" class="floating-label peer-focus:-translate-y-5 peer-focus:scale-90 -translate-y-5 scale-90">
                                Descriptif
                            </label> -->
                            <FloatingInput
                                name="description"
                                title="Description"
                                :input="description"
                                :error="descriptionError"
                                @value="(val:string) => setDescription(val)"
                                isRequired
                            />
                        </div>
                    </div>
                    
                    <!-- Partie Droite de la page -->
                    <div class="w-full md:w-[35%] h-full pl-4 md:block w-max item-center">
                        <!-- Input Text Nom Event -->
                        <div class="relative w-full">
                            <!-- <input id="eventName" type="text" class="border-white/30 focus:border-white appearance-none floating-input peer" required>
                            <label for="eventName" class="floating-label peer-focus:-translate-y-5 peer-focus:scale-90 -translate-y-5 scale-90">
                                Nom de l'évenement
                            </label> -->
                            <FloatingInput
                                name="eventName"
                                title="Nom de l'évenement"
                                :input="eventName"
                                :error="eventNameError"
                                @value="(val:string) => setEventName(val)"
                                isRequired
                            />
                        </div>
                        
                        <!-- Scroll Menu Catégorie -->
                        <!-- <select name="category_selector" data-placeholder="Choississez une catégorie" required>
                            <option value="musique">Musique</option>
                            <option value="cuisinedumonde">Cuisine du Monde</option>
                            <option value="escapegame">Escape Game</option>
                        </select> -->

                        <!-- Ajouter Catégorie -->
                        <div class="relative w-full">
                            <!-- <input id="addCategory" type="text" class="border border-white/30 focus:border-white appearance-none floating-input peer">
                            <label for="addCategory" class="floating-label peer-focus:-translate-y-5 peer-focus:scale-90 -translate-y-5 scale-90">
                                Ajout de catégorie
                            </label> -->

                            <FloatingInput
                                name="category"
                                title="Catégorie"
                                :input="category"
                                :error="categoryError"
                                @value="(val:string) => setCategory(val)"
                                isRequired
                                list="categorylist"
                            />
                            <datalist id="categorylist">
                                <option v-for="category in categories">{{category}}</option>
                            </datalist>
                        </div>
                        
                        <!-- Prix (Optionnel) -->
                        <div class="relative w-full">
                            <FloatingInput
                                name="price"
                                title="Prix € (Optionnel)"
                                :input="price"
                                :error="priceError"
                                @value="(val:number) => setPrice(val)"
                            />
                        </div>

                        <div class="relative w-full">
                            <Datepicker v-model="date" range multiCalendars required />
                        </div>

                        <!-- Bouton import image -->
                        <label class="custom-file-upload">
                            <i class="fa-solid fa-image"></i> Importer une image <span style="color:#EC6F47">*</span>
                            <input type="file" @change="onImageFileSelected" accept="image/jpeg" required />
                        </label>
                        <!-- Bouton Valider -->
                        <button
                            :disabled="isCreating || !validInput"
                            type="submit"
                            class="w-full h-14 bg-epic-blue text-[13px] rounded uppercase font-bold disabled:cursor-default disabled:opacity-40 hover:brightness-110"
                            @click="createNewEvent">
                        <p v-if="isCreating">Création en cours ...</p>
                        <p v-else>Valider</p>
                        </button>
                        <p class="text-red-400" v-if="errMsg">{{ errMsg }}</p>
                    </div>                        
                </div>
            </div>
        </main>
    </MainLayout>
</template>

<style scoped>
    @import "https://unpkg.com/@vuepic/vue-datepicker@latest/dist/main.css";

    .border {
        border: dashed;
        border-color: rgba(51, 55, 57, 0.3)
    }

    .item-center {
    display: grid;
    gap: 4px;
    align-items: center;
    justify-items: center;
    }

    input::-webkit-outer-spin-button,
    input::-webkit-inner-spin-button {
        -webkit-appearance: none;
        margin: 0;
    }

    input[type="file"] {
        display: none;
    } 
    .custom-file-upload {
        font-weight: 700;
        display: inline-block;
        padding: 6px 12px;
        cursor: pointer;
    }
    .custom-file-upload:active {
        color: rgba(0, 125, 252, var(--tw-bg-opacity));
    }
    
    .custom-file-upload:hover::after {
        width: 100%;
    }

    .custom-file-upload::after {  
        content: '';
        width: 0px;
        height: 3px;
        display: block;
        background: rgba(0, 125, 252, var(--tw-bg-opacity));
        transition: 300ms;
    }

    .espace {
        margin-bottom: 20px;
    }
</style>
